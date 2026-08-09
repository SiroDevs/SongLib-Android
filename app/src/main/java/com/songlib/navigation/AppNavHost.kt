package com.songlib.navigation

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.songlib.core.common.utils.Routes
import com.songlib.core.data.repos.PreferencesRepo
import com.songlib.core.data.repos.ThemeRepo
import com.songlib.core.database.model.BookEntity
import com.songlib.core.database.model.DraftEntity
import com.songlib.core.database.model.ListingUi
import com.songlib.core.database.model.SongEntity
import com.songlib.feature.donation.viewmodel.DonationViewModel
import com.songlib.feature.donation.view.screens.DonationScreen
import com.songlib.feature.donation.view.screens.PaymentWebViewScreen
import com.songlib.feature.casting.viewmodel.CastingViewModel
import com.songlib.feature.casting.view.screen.CastingScreen
import com.songlib.feature.edits.admin.viewmodel.AdminEditsViewModel
import com.songlib.feature.edits.admin.view.screen.AdminEditsScreen
import com.songlib.feature.drafts.list.viewmodel.DraftsViewModel
import com.songlib.feature.drafts.present.viewmodel.DraftPresenterViewModel
import com.songlib.feature.drafts.list.view.DraftsScreen
import com.songlib.feature.drafts.present.view.DraftPresenterScreen
import com.songlib.feature.edits.user.viewmodel.EditsViewModel
import com.songlib.feature.edits.user.view.EditsScreen
import com.songlib.feature.help.view.HelpScreen
import com.songlib.feature.history.viewmodel.HistoryViewModel
import com.songlib.feature.history.view.screen.HistoryScreen
import com.songlib.feature.home.viewmodel.HomeViewModel
import com.songlib.feature.home.view.screen.HomeScreen
import com.songlib.feature.how_it_works.view.HowItWorksScreen
import com.songlib.feature.listing.viewmodel.ListingViewModel
import com.songlib.feature.listing.view.ListingScreen
import com.songlib.feature.song.presentor.viewmodel.PresenterViewModel
import com.songlib.feature.song.presentor.view.screen.PresenterScreen
import com.songlib.feature.selection.viewmodel.SelectionViewModel
import com.songlib.feature.selection.view.screen.SelectionScreen
import com.songlib.feature.settings.viewmodel.SettingsViewModel
import com.songlib.feature.settings.view.screen.SettingsScreen
import com.songlib.feature.account.viewmodel.AccountViewModel
import com.songlib.feature.account.view.screen.AccountScreen
import com.songlib.feature.song.editor.viewmodel.EditorViewModel
import com.songlib.feature.song.editor.view.EditorScreen
import com.songlib.viewmodel.MainViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalComposeUiApi::class, ExperimentalFoundationApi::class)
@Composable
fun AppNavHost(
    navController: NavHostController = rememberNavController(),
    themeRepo: ThemeRepo,
    prefsRepo: PreferencesRepo,
    mainViewModel: MainViewModel,
    onSignInRequest: (
        callback: (googleId: String, email: String, name: String, photo: String) -> Unit,
        onError: (message: String) -> Unit
    ) -> Unit,
) {
    val isReady by mainViewModel.isReady.collectAsState()
    val destination by mainViewModel.destination.collectAsState()

    if (!isReady) return

    val startDestination = when (destination) {
        is MainViewModel.Destination.Selection -> Routes.SELECTION
        is MainViewModel.Destination.Home -> Routes.HOME
    }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(
            route = Routes.SELECTION_ROUTE_PATTERN,
            arguments = listOf(
                navArgument(Routes.SELECTION_AUTO_ARG) {
                    type = NavType.BoolType
                    defaultValue = false
                }
            )
        ) { backStackEntry ->
            val autoRecover = backStackEntry.arguments?.getBoolean(Routes.SELECTION_AUTO_ARG) == true
            val viewModel: SelectionViewModel = hiltViewModel()
            SelectionScreen(
                navController = navController,
                viewModel = viewModel,
                themeRepo = themeRepo,
                autoRecover = autoRecover,
            )
        }

        composable(Routes.HOME) {
            val homeViewModel: HomeViewModel = hiltViewModel()
            HomeScreen(
                navController = navController,
                homeViewModel = homeViewModel,
                prefsRepo = prefsRepo,
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
                prefsRepo = prefsRepo
            )
        }

        composable(Routes.EDITOR) {
            val song  = navController.previousBackStackEntry
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

        composable(Routes.DRAFT_PRESENT) {
            val draft = navController.previousBackStackEntry
                ?.savedStateHandle?.get<DraftEntity>("draft")
            val horizontalSlides = prefsRepo.horizontalSlides
            val viewModel: DraftPresenterViewModel = hiltViewModel()
            if (draft != null) {
                DraftPresenterScreen(
                    navController = navController,
                    draft = draft,
                    horizontalSlides = horizontalSlides,
                    viewModel = viewModel,
                )
            } else {
                navController.popBackStack()
            }
        }

        composable(Routes.DRAFT_EDITOR) {
            val draft = navController.previousBackStackEntry
                ?.savedStateHandle?.get<DraftEntity>("draft_to_edit")
            val viewModel: EditorViewModel = hiltViewModel()
            if (draft != null) {
                EditorScreen(
                    navController = navController,
                    draft = draft,
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
            val settViewModel: SettingsViewModel = hiltViewModel()
            SettingsScreen(
                navController = navController,
                settViewModel = settViewModel,
                themeRepo = themeRepo,
                onReset = { mainViewModel.reset() },
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

        composable(
            route = Routes.PAYMENT_WEBVIEW,
            arguments = listOf(
                navArgument("redirectUrl") { type = NavType.StringType }
            ),
        ) { backStackEntry ->
            val encoded = backStackEntry.arguments?.getString("redirectUrl") ?: ""
            val redirectUrl = Routes.decodeRedirectUrl(encoded)

            val donationEntry = remember(backStackEntry) {
                navController.getBackStackEntry(Routes.DONATION)
            }
            val viewModel: DonationViewModel = hiltViewModel(donationEntry)
            val scope = rememberCoroutineScope()

            PaymentWebViewScreen(
                navController = navController,
                viewModel = viewModel,
                redirectUrl = redirectUrl,
                onPaymentComplete = { isSuccess ->
                    if (isSuccess) {
                        scope.launch { prefsRepo.recordDonation() }
                        navController.navigate(Routes.HOME) {
                            popUpTo(Routes.HOME) { inclusive = false }
                        }
                    } else {
                        viewModel.resetState()
                        navController.popBackStack()
                    }
                },
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

        composable(Routes.ACCOUNT) {
            val viewModel: AccountViewModel = hiltViewModel()
            AccountScreen(
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

        composable(Routes.CASTING) {
            val viewModel: CastingViewModel = hiltViewModel()
            CastingScreen(navController = navController, viewModel = viewModel)
        }
    }
}