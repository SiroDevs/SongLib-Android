package com.songlib.app.navigation

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.songlib.core.data.repos.ThemeRepo
import com.songlib.feature.home.HomeViewModel
import com.songlib.feature.home.view.HomeScreen
import com.songlib.feature.listing.ListingViewModel
import com.songlib.feature.listing.view.ListingScreen
import com.songlib.core.common.utils.Routes
import com.songlib.core.database.model.ListingUi
import com.songlib.core.database.model.SongEntity
import com.songlib.feature.presenter.PresenterViewModel
import com.songlib.feature.presenter.view.PresenterScreen
import com.songlib.feature.selection.SelectionViewModel
import com.songlib.feature.selection.view.SelectionScreen
import com.songlib.feature.settings.SettingsViewModel
import com.songlib.feature.settings.view.SettingsScreen
import com.songlib.feature.splash.SplashViewModel
import com.songlib.feature.splash.view.SplashScreen

@OptIn(ExperimentalComposeUiApi::class, ExperimentalFoundationApi::class)
@Composable
fun AppNavHost(
    navController: NavHostController = rememberNavController(),
    themeRepo: ThemeRepo
) {
    NavHost(
        navController = navController,
        startDestination = Routes.SPLASH
    ) {

        composable(Routes.SPLASH) {
            val viewModel: SplashViewModel = hiltViewModel()
            SplashScreen(navController = navController, viewModel = viewModel)
        }

        composable(Routes.SELECTION) {
            val viewModel: SelectionViewModel = hiltViewModel()
            SelectionScreen(
                navController = navController,
                viewModel = viewModel,
                themeRepo = themeRepo,
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
                ?.get<SongEntity>("song")

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