package com.songlib.presentation.navigation

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.*
import androidx.navigation.compose.*
import com.google.gson.Gson
import com.songlib.presentation.screens.home.HomeScreen
import com.songlib.presentation.screens.selection.*
import com.songlib.presentation.screens.splash.SplashScreen

@OptIn(ExperimentalComposeUiApi::class, ExperimentalFoundationApi::class)
@Composable
fun AppNavHost(
    navController: NavHostController = rememberNavController(),
    //homeViewModel: HomeViewModel,
) {
    NavHost(
        navController = navController,
        startDestination = Routes.SPLASH
    ) {

        composable(Routes.SPLASH) {
            SplashScreen()
        }

        composable(Routes.STEP_1) {
            Step1Screen()
        }

        composable(Routes.STEP_2) {
            Step2Screen()
        }

        composable(Routes.HOME) {
            HomeScreen()
        }
    }
}