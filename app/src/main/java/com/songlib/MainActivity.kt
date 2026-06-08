package com.songlib

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.songlib.app.navigation.AppNavHost
import com.songlib.core.data.repos.PrefsRepo
import com.songlib.core.data.repos.ThemeMode
import com.songlib.core.data.repos.ThemeRepo
import com.songlib.core.designsystem.theme.AppTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var prefsRepo: PrefsRepo

    private var signInCallback: ((googleId: String, email: String, name: String, photo: String) -> Unit)? = null

    private val googleSignInClient by lazy {
        GoogleSignIn.getClient(
            this,
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestProfile()
                .requestIdToken(BuildConfig.GOOGLE_WEB_CLIENT_ID)
                .build()
        )
    }

    private val signInLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)
            signInCallback?.invoke(
                account.id ?: "",
                account.email ?: "",
                account.displayName ?: "",
                account.photoUrl?.toString() ?: ""
            )
        } catch (e: ApiException) {
            // Sign-in failed — callback not invoked; ViewModel will remain in Loading → caller should handle timeout
        }
        signInCallback = null
    }

    fun launchSignIn(callback: (googleId: String, email: String, name: String, photo: String) -> Unit) {
        signInCallback = callback
        signInLauncher.launch(googleSignInClient.signInIntent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val themeRepo: ThemeRepo = hiltViewModel()
            val themeMode = themeRepo.selectedTheme
            val isDarkTheme = when (themeMode) {
                ThemeMode.DARK   -> true
                ThemeMode.LIGHT  -> false
                ThemeMode.SYSTEM -> isSystemInDarkTheme()
            }

            AppTheme(useDarkTheme = isDarkTheme) {
                AppNavHost(
                    themeRepo       = themeRepo,
                    prefsRepo       = prefsRepo,
                    onSignInRequest = ::launchSignIn
                )
            }
        }
    }
}
