package com.songlib.presentation.navigation

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.*
import androidx.navigation.compose.*
import com.songlib.data.models.*
import com.songlib.domain.repos.ThemeRepository
import com.songlib.presentation.home.HomeViewModel
import com.songlib.presentation.home.view.HomeScreen
import com.songlib.presentation.listing.ListingViewModel
import com.songlib.presentation.listing.view.ListingScreen
import com.songlib.presentation.presenter.PresenterViewModel
import com.songlib.presentation.presenter.view.PresenterScreen
import com.songlib.presentation.selection.step1.Step1ViewModel
import com.songlib.presentation.selection.step1.view.Step1Screen
import com.songlib.presentation.selection.step2.Step2ViewModel
import com.songlib.presentation.selection.step2.view.Step2Screen
import com.songlib.presentation.settings.SettingsViewModel
import com.songlib.presentation.settings.view.SettingsScreen
import com.songlib.presentation.splash.SplashViewModel
import com.songlib.presentation.splash.view.SplashScreen

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
            val viewModel: SplashViewModel = hiltViewModel()
            SplashScreen(navController = navController, viewModel = viewModel)
        }

        composable(Routes.STEP_1) {
            val viewModel: Step1ViewModel = hiltViewModel()
            Step1Screen(
                navController = navController,
                viewModel = viewModel,
                themeRepo = themeRepo,
            )
        }

        composable(Routes.STEP_2) {
            val viewModel: Step2ViewModel = hiltViewModel()
            Step2Screen(
                navController = navController,
                viewModel = viewModel,
            )
        }

        composable(Routes.HOME) {
            val viewModel: HomeViewModel = hiltViewModel()
            HomeScreen(
                navController = navController,
                viewModel = viewModel,
            )
        }

        composable(route = Routes.PRESENTER) {
            val song = navController.previousBackStackEntry
                ?.savedStateHandle
                ?.get<Song>("song")

            val viewModel: PresenterViewModel = hiltViewModel()

            PresenterScreen(
                navController = navController,
                viewModel = viewModel,
                song = song,
            )
        }

        composable(route = Routes.LISTING) {
            val listing = navController.previousBackStackEntry
                ?.savedStateHandle
                ?.get<ListingUi>("listing")

            val viewModel: ListingViewModel = hiltViewModel()

            ListingScreen(
                navController = navController,
                viewModel = viewModel,
                listing = listing,
            )
        }

        composable(Routes.SETTINGS) {
            val viewModel: SettingsViewModel = hiltViewModel()
            SettingsScreen(
                navController = navController,
                viewModel = viewModel,
                themeRepo = themeRepo,
            )
        }
    }
}