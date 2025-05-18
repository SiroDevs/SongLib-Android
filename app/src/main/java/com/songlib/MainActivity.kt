package com.songlib

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.Keep
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import com.songlib.core.utils.PrefConstants
import com.songlib.presentation.navigation.AppNavHost
import com.songlib.presentation.navigation.Routes
import com.songlib.presentation.theme.SongLibTheme
import dagger.hilt.android.AndroidEntryPoint

@ExperimentalComposeUiApi
@ExperimentalFoundationApi
@Keep
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val prefs = getSharedPreferences(PrefConstants.PREFERENCE_FILE, MODE_PRIVATE)
        val isDataSelected = prefs.getBoolean(PrefConstants.DATA_SELECTED, false)
        val isDataLoaded = prefs.getBoolean(PrefConstants.DATA_LOADED, false)

        val startDestination = when {
            isDataLoaded -> Routes.HOME
            isDataSelected -> Routes.STEP_2
            else -> Routes.STEP_1
        }
        enableEdgeToEdge()
        setContent {
            SongLibTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavHost(startDestination = startDestination)
                }
            }
        }
    }
}