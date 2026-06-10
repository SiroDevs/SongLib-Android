package com.songlib

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.songlib.app.navigation.AppNavHost
import com.songlib.core.data.repos.PrefsRepo
import com.songlib.core.data.repos.ThemeMode
import com.songlib.core.data.repos.ThemeRepo
import com.songlib.core.designsystem.theme.AppTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import io.sentry.Sentry

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var prefsRepo: PrefsRepo

    private val credentialManager by lazy { CredentialManager.create(this) }

    fun launchSignIn(
        callback: (googleId: String, email: String, name: String, photo: String) -> Unit
    ) {
        val googleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(BuildConfig.GoogleWebClientId)
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
                    callback(
                        googleIdTokenCredential.id,                          // googleId
                        googleIdTokenCredential.id,                          // email (same as id for Google)
                        googleIdTokenCredential.displayName ?: "",           // name
                        googleIdTokenCredential.profilePictureUri?.toString() ?: ""  // photo
                    )
                }
            } catch (e: GetCredentialException) {
                // User cancelled or no accounts available — silently ignore
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    // waiting for view to draw to better represent a captured error with a screenshot
    findViewById<android.view.View>(android.R.id.content).viewTreeObserver.addOnGlobalLayoutListener {
      try {
        throw Exception("This app uses Sentry! :)")
      } catch (e: Exception) {
        Sentry.captureException(e)
      }
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
                    onSignInRequest = ::launchSignIn
                )
            }
        }
    }
}