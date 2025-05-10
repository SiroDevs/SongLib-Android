package com.songlib

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.songlib.core.utils.PrefConstants
import com.songlib.presentation.navigation.Routes
import com.songlib.presentation.theme.SongLibTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val prefs = getSharedPreferences(PrefConstants.Key.PREFERENCE_FILE, MODE_PRIVATE)
        val isDataSelected = prefs.getBoolean(PrefConstants.Key.DATA_SELECTED, false)
        val isDataLoaded = prefs.getBoolean(PrefConstants.Key.DATA_LOADED, false)

        setContent {
            SongLibTheme {
                val navController: NavHostController = rememberNavController()
                if (isDataLoaded) {
                    navController.navigate(Routes.HOME)
                } else {
                    navController.navigate(if (isDataSelected) Routes.STEP_2 else Routes.STEP_1)
                }
            }
        }
    }
}