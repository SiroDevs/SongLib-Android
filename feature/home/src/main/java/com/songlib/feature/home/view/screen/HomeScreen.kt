package com.songlib.feature.home.view.screen

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import com.songlib.core.common.entity.UiState
import com.songlib.core.common.utils.Routes
import com.songlib.core.data.repos.PreferencesRepo
import com.songlib.core.ui.components.indicators.EmptyState
import com.songlib.core.ui.components.indicators.ErrorState
import com.songlib.feature.home.viewmodel.HomeViewModel
import com.songlib.core.ui.components.action.AppTopBar
import com.songlib.feature.home.view.components.HomeSkeleton
import kotlinx.coroutines.flow.collectLatest

@Composable
fun HomeScreen(
    navController: NavHostController,
    homeViewModel: HomeViewModel,
    prefsRepo: PreferencesRepo,
) {
    val uiState by homeViewModel.uiState.collectAsState()
    val songs by homeViewModel.songs.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        homeViewModel.toastEvent.collectLatest { msg ->
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(Unit) { homeViewModel.fetchData() }

    val isIncompleteLibrary = uiState == UiState.Filtered &&
            songs.isEmpty() &&
            prefsRepo.selectedBooks.isNotEmpty()

    LaunchedEffect(isIncompleteLibrary) {
        if (isIncompleteLibrary) {
            homeViewModel.recoverIncompleteLibrary()
            navController.navigate(Routes.SELECTION_AUTO_RECOVER) {
                popUpTo(navController.graph.id) { inclusive = true }
                launchSingleTop = true
            }
        }
    }

    when (uiState) {
        is UiState.Error -> Scaffold(
            topBar = { AppTopBar(title = "SongLib") }
        ) { padding ->
            Box(modifier = Modifier.padding(padding)) {
                ErrorState(
                    message = (uiState as UiState.Error).message,
                    retryAction = { homeViewModel.fetchData() },
                )
            }
        }

        UiState.Loading -> HomeSkeleton()

        UiState.Filtered -> {
            if (songs.isEmpty()) {
                if (prefsRepo.selectedBooks.isNotEmpty()) {
                    HomeSkeleton()
                } else {
                    Scaffold(
                        topBar = { AppTopBar(title = "SongLib") }
                    ) { padding ->
                        Box(modifier = Modifier.padding(padding)) {
                            EmptyState(
                                message = "It appears you didn't finish your songbook selection, " +
                                        "that's why it's empty here at the moment.\n\nLet's fix that asap!",
                                messageIcon = Icons.Default.EditNote,
                                onAction = {
                                    homeViewModel.recoverIncompleteLibrary()
                                    navController.navigate(Routes.SELECTION_AUTO_RECOVER) {
                                        popUpTo(navController.graph.id) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            )
                        }
                    }
                }
            } else {
                HomeContent(
                    viewModel = homeViewModel,
                    navController = navController,
                    prefsRepo = prefsRepo,
                )
            }
        }

        else -> HomeSkeleton()
    }
}