package com.songlib

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.songlib.core.utils.PrefConstants
import com.songlib.presentation.screens.home.HomeScreen
import com.songlib.presentation.screens.selection.*
import com.songlib.presentation.theme.SongLibTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val prefs = getSharedPreferences(PrefConstants.Key.PREFERENCE_FILE, MODE_PRIVATE)
        val isDataSelected = prefs.getBoolean(PrefConstants.Key.DATA_SELECTED, false)
        val isDataLoaded = prefs.getBoolean(PrefConstants.Key.DATA_LOADED, false)

        setContent {
            SongLibTheme {
                if (isDataLoaded) {
                    HomeScreen()
                } else {
                    if (isDataSelected) {
                        Step2Screen()
                    } else {
                        Step1Screen()
                    }
                }
            }
        }
    }
}