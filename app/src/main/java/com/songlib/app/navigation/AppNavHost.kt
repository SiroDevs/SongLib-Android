package com.songlib.app.navigation

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.songlib.core.common.utils.Routes
import com.songlib.core.data.repos.PrefsRepo
import com.songlib.core.data.repos.ThemeRepo
import com.songlib.core.database.model.BookEntity
import com.songlib.core.database.model.ListingUi
import com.songlib.core.database.model.SongEntity
import com.songlib.feature.donation.DonationViewModel
import com.songlib.feature.donation.view.DonationScreen
import com.songlib.feature.edits.admin.AdminEditsViewModel
import com.songlib.feature.edits.admin.view.AdminEditsScreen
import com.songlib.feature.drafts.DraftsViewModel
import com.songlib.feature.drafts.view.DraftsScreen
import com.songlib.feature.edits.user.EditsViewModel
import com.songlib.feature.edits.user.view.EditsScreen
import com.songlib.feature.help.view.HelpScreen
import com.songlib.feature.home.HistoryViewModel
import com.songlib.feature.home.HomeViewModel
import com.songlib.feature.home.view.HistoryScreen
import com.songlib.feature.home.view.HomeScreen
import com.songlib.feature.howitworks.view.HowItWorksScreen
import com.songlib.feature.listing.ListingViewModel
import com.songlib.feature.listing.view.ListingScreen
import com.songlib.feature.song.presentor.PresenterViewModel
import com.songlib.feature.song.presentor.view.PresenterScreen
import com.songlib.feature.selection.SelectionViewModel
import com.songlib.feature.selection.view.SelectionScreen
import com.songlib.feature.settings.SettingsViewModel
import com.songlib.feature.settings.UserProfileViewModel
import com.songlib.feature.settings.view.SettingsScreen
import com.songlib.feature.settings.view.UserProfileScreen
import com.songlib.feature.song.editor.EditorViewModel
import com.songlib.feature.song.editor.view.EditorScreen
import com.songlib.feature.splash.SplashViewModel
import com.songlib.feature.splash.view.SplashScreen

@OptIn(ExperimentalComposeUiApi::class, ExperimentalFoundationApi::class)
@Composable
fun AppNavHost(
    navController: NavHostController = rememberNavController(),
    themeRepo: ThemeRepo,
    prefsRepo: PrefsRepo,
    onSignInRequest: (callback: (googleId: String, email: String, name: String, photo: String) -> Unit) -> Unit,
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
                themeRepo = themeRepo,
                prefsRepo = prefsRepo
            )
        }

        composable(Routes.PRESENT) {
            val book = navController.previousBackStackEntry
                ?.savedStateHandle?.get<BookEntity>("book")
            val song = navController.previousBackStackEntry
                ?.savedStateHandle?.get<SongEntity>("song")
            val viewModel: PresenterViewModel = hiltViewModel()
            PresenterScreen(
                navController = navController,
                viewModel = viewModel,
                book = book,
                song = song,
                themeRepo = themeRepo,
                prefsRepo = prefsRepo
            )
        }

        composable(Routes.EDITOR) {
            val song = navController.previousBackStackEntry
                ?.savedStateHandle?.get<SongEntity>("song_to_edit")
            val viewModel: EditorViewModel = hiltViewModel()
            if (song != null) {
                EditorScreen(
                    navController = navController,
                    song = song,
                    viewModel = viewModel,
                )
            } else {
                navController.popBackStack()
            }
        }

        composable(Routes.LISTING) {
            val listing = navController.previousBackStackEntry
                ?.savedStateHandle?.get<ListingUi>("listing")
            val viewModel: ListingViewModel = hiltViewModel()
            ListingScreen(
                navController = navController,
                viewModel = viewModel,
                listing = listing,
                prefsRepo = prefsRepo
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

        composable(Routes.HOW_IT_WORKS) {
            HowItWorksScreen(navController = navController)
        }

        composable(Routes.HELP) {
            HelpScreen(navController = navController)
        }

        composable(Routes.DONATION) {
            val viewModel: DonationViewModel = hiltViewModel()
            DonationScreen(
                navController = navController,
                viewModel = viewModel,
            )
        }

        composable(Routes.DRAFTS) {
            val viewModel: DraftsViewModel = hiltViewModel()
            DraftsScreen(navController = navController, viewModel = viewModel)
        }

        composable(Routes.HISTORY) {
            val viewModel: HistoryViewModel = hiltViewModel()
            HistoryScreen(navController = navController, viewModel = viewModel)
        }

        composable(Routes.USER_PROFILE) {
            val viewModel: UserProfileViewModel = hiltViewModel()
            UserProfileScreen(
                navController = navController,
                viewModel = viewModel,
                onSignInRequested = onSignInRequest
            )
        }

        composable(Routes.USER_EDITS) {
            val viewModel: EditsViewModel = hiltViewModel()
            EditsScreen(
                navController = navController,
                prefsRepo = prefsRepo,
                viewModel = viewModel,
            )
        }

        composable(Routes.ADMIN_EDITS) {
            val viewModel: AdminEditsViewModel = hiltViewModel()
            AdminEditsScreen(navController = navController, viewModel = viewModel)
        }
    }
}
