package com.songlib.presentation.navigation

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.*
import androidx.navigation.compose.*
import com.songlib.data.models.Song
import com.songlib.domain.repository.ThemeRepository
import com.songlib.presentation.screens.home.HomeScreen
import com.songlib.presentation.screens.settings.SettingsScreen
import com.songlib.presentation.screens.presenter.PresenterScreen
import com.songlib.presentation.screens.selection.step1.Step1Screen
import com.songlib.presentation.screens.selection.step2.Step2Screen
import com.songlib.presentation.screens.splash.SplashScreen
import com.songlib.presentation.viewmodels.*

@OptIn(ExperimentalComposeUiApi::class, ExperimentalFoundationApi::class)
@Composable
fun AppNavHost(
    navController: NavHostController = rememberNavController(),
    themeRepo: ThemeRepository
) {
    NavHost(
        navController = navController,
        startDestination = Routes.SPLASH
    ) {

        composable(Routes.SPLASH) {
            SplashScreen(navController = navController)
        }

        composable(Routes.STEP_1) {
            val viewModel: SelectionViewModel = hiltViewModel()
            Step1Screen(
                viewModel = viewModel,
                navController = navController,
                themeRepo = themeRepo,
            )
        }

        composable(Routes.STEP_2) {
            val viewModel: SelectionViewModel = hiltViewModel()
            Step2Screen(
                viewModel = viewModel,
                navController = navController,
            )
        }

        composable(Routes.HOME) {
            val viewModel: HomeViewModel = hiltViewModel()
            HomeScreen(
                viewModel = viewModel,
                navController = navController,
            )
        }

        composable(route = Routes.PRESENTER) {
            val song = navController.previousBackStackEntry
                ?.savedStateHandle
                ?.get<Song>("song")

            val viewModel: PresenterViewModel = hiltViewModel()

            PresenterScreen(
                viewModel = viewModel,
                navController = navController,
                song = song,
            )
        }

        composable(Routes.SETTINGS) {
            SettingsScreen(
                navController = navController,
                themeRepo = themeRepo,
            )
        }

    }
}