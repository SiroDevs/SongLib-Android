package com.songlib

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.songlib.navigation.AppNavHost
import com.songlib.core.data.repos.PreferencesRepo
import com.songlib.core.data.repos.ThemeMode
import com.songlib.core.data.repos.ThemeRepo
import com.songlib.core.design_system.theme.AppTheme
import com.songlib.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var prefsRepo: PreferencesRepo

    private val credentialManager by lazy { CredentialManager.create(this) }
    private val firebaseAuth by lazy { FirebaseAuth.getInstance() }

    fun launchSignIn(
        callback: (googleId: String, email: String, name: String, photo: String) -> Unit,
        onError: (message: String) -> Unit = {}
    ) {
        val webClientId = getString(R.string.default_web_client_id)
        if (webClientId.isBlank()) {
            android.util.Log.e("SignIn", "default_web_client_id is blank — check app/google-services.json")
            onError("Google sign-in isn't configured correctly. Please contact support.")
            return
        }

        val googleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(webClientId)
            .setAutoSelectEnabled(false)
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        CoroutineScope(Dispatchers.Main).launch {
            try {
                val result = credentialManager.getCredential(
                    request = request,
                    context = this@MainActivity,
                )
                val credential = result.credential
                if (credential is CustomCredential &&
                    credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
                ) {
                    val googleIdTokenCredential =
                        GoogleIdTokenCredential.createFrom(credential.data)

                    val firebaseCredential = GoogleAuthProvider.getCredential(
                        googleIdTokenCredential.idToken,
                        null
                    )
                    firebaseAuth.signInWithCredential(firebaseCredential)
                        .addOnSuccessListener { authResult ->
                            val firebaseUser = authResult.user
                            if (firebaseUser == null) {
                                onError("Sign-in succeeded but no user was returned.")
                                return@addOnSuccessListener
                            }
                            callback(
                                firebaseUser.email ?: googleIdTokenCredential.id,
                                firebaseUser.email ?: googleIdTokenCredential.id,
                                firebaseUser.displayName ?: googleIdTokenCredential.displayName ?: "",
                                firebaseUser.photoUrl?.toString()
                                    ?: googleIdTokenCredential.profilePictureUri?.toString()
                                    ?: ""
                            )
                        }
                        .addOnFailureListener { e ->
                            android.util.Log.e("SignIn", "Firebase signInWithCredential failed", e)
                            onError(e.message ?: "Google sign-in failed. Please try again.")
                        }
                } else {
                    android.util.Log.w("SignIn", "Unexpected credential type: ${credential.type}")
                    onError("Sign-in returned an unexpected credential type.")
                }
            } catch (e: GetCredentialException) {
                android.util.Log.e("SignIn", "Google sign-in failed: ${e.javaClass.simpleName} ${e.message}", e)
                onError(e.message ?: "Google sign-in was cancelled or failed.")
            } catch (e: Exception) {
                android.util.Log.e("SignIn", "Unexpected error during sign-in", e)
                onError("Something went wrong signing in. Please try again.")
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        val mainViewModel: MainViewModel by viewModels()

        splashScreen.setKeepOnScreenCondition {
            !mainViewModel.isReady.value
        }

        setContent {
            val themeRepo: ThemeRepo = hiltViewModel()
            val themeMode = themeRepo.selectedTheme
            val isDarkTheme = when (themeMode) {
                ThemeMode.DARK -> true
                ThemeMode.LIGHT -> false
                ThemeMode.SYSTEM -> isSystemInDarkTheme()
            }

            AppTheme(useDarkTheme = isDarkTheme) {
                AppNavHost(
                    themeRepo = themeRepo,
                    prefsRepo = prefsRepo,
                    mainViewModel = mainViewModel,
                    onSignInRequest = { onResult, onError -> launchSignIn(onResult, onError) }
                )
            }
        }
    }
}
