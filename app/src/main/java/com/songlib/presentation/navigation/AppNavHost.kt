package com.songlib.presentation.navigation

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.*
import androidx.navigation.compose.*
import com.songlib.data.models.Song
import com.songlib.presentation.screens.home.HomeScreen
import com.songlib.presentation.screens.presentor.PresentorScreen
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

        composable(Routes.PRESENTOR) {
            val presentorViewModel: PresentorViewModel = hiltViewModel()
            val navBackStackEntry = remember { navController.currentBackStackEntry }
            val song = navBackStackEntry?.savedStateHandle?.get<Song>("song")

            if (song != null) {
                PresentorScreen(
                    viewModel = presentorViewModel,
                    navController = navController,
                    onBackPressed = { navController.popBackStack() },
                    song = song
                )
            }
        }

    }
}