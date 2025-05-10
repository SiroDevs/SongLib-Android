package com.songlib.presentation.navigation

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.*
import androidx.navigation.compose.*
import com.songlib.presentation.screens.home.HomeScreen
import com.songlib.presentation.screens.selection.*
import com.songlib.presentation.screens.splash.SplashScreen
import com.songlib.presentation.viewmodels.*

@OptIn(ExperimentalComposeUiApi::class, ExperimentalFoundationApi::class)
@Composable
fun AppNavHost(
    startDestination: String,
    navController: NavHostController = rememberNavController(),
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {

        composable(Routes.SPLASH) {
            SplashScreen()
        }

        composable(Routes.STEP_1) {
            val selectionViewModel: SelectionViewModel = hiltViewModel()
            Step1Screen(
                viewModel = selectionViewModel,
                navController = navController,
            )
        }

        composable(Routes.STEP_2) {
            val selectionViewModel: SelectionViewModel = hiltViewModel()
            Step2Screen(
                viewModel = selectionViewModel,
                navController = navController,
            )
        }

        composable(Routes.HOME) {
            val homeViewModel: HomeViewModel = hiltViewModel()
            HomeScreen(
                viewModel = homeViewModel,
                navController = navController,
            )
        }
    }
}