package com.songlib

import android.animation.*
import android.os.*
import android.view.View
import android.view.animation.DecelerateInterpolator
import androidx.activity.*
import androidx.activity.compose.setContent
import androidx.annotation.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.ui.*
import androidx.core.animation.doOnEnd
import androidx.hilt.navigation.compose.hiltViewModel
import com.songlib.domain.repository.*
import com.songlib.presentation.navigation.*
import com.songlib.presentation.theme.*
import dagger.hilt.android.AndroidEntryPoint

@ExperimentalComposeUiApi
@ExperimentalFoundationApi
@Keep
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        splashScreen.setOnExitAnimationListener { splashScreenView ->
            val scaleX = ObjectAnimator.ofFloat(splashScreenView, View.SCALE_X, 1f, 0.7f)
            val scaleY = ObjectAnimator.ofFloat(splashScreenView, View.SCALE_Y, 1f, 0.7f)
            val fadeOut = ObjectAnimator.ofFloat(splashScreenView, View.ALPHA, 1f, 0f)

            AnimatorSet().apply {
                playTogether(scaleX, scaleY, fadeOut)
                interpolator = DecelerateInterpolator()
                duration = 500L
                doOnEnd { splashScreenView.remove() }
                start()
            }
        }

        setContent {
            val themeRepo: ThemeRepository = hiltViewModel()
            val themeMode = themeRepo.selectedTheme
            val isDarkTheme = when (themeMode) {
                ThemeMode.DARK -> true
                ThemeMode.LIGHT -> false
                ThemeMode.SYSTEM -> isSystemInDarkTheme()
            }

            AppTheme(useDarkTheme = isDarkTheme) {
                AppNavHost(themeRepo = themeRepo)
            }
        }
    }
}