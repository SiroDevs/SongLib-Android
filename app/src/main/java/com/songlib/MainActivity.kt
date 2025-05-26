package com.songlib

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.animation.AnticipateInterpolator
import android.view.animation.DecelerateInterpolator
import androidx.activity.*
import androidx.activity.compose.setContent
import androidx.annotation.Keep
import androidx.annotation.RequiresApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.ui.*
import androidx.core.animation.doOnEnd
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.songlib.presentation.navigation.*
import com.songlib.presentation.theme.SongLibTheme
import dagger.hilt.android.AndroidEntryPoint

@ExperimentalComposeUiApi
@ExperimentalFoundationApi
@Keep
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen().setKeepOnScreenCondition { false }
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
            SongLibTheme {
                AppNavHost()
            }
        }
    }
}