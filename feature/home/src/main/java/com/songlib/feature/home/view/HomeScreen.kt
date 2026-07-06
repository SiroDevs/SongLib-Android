package com.songlib.feature.home.view

import android.widget.Toast
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import com.songlib.core.common.entity.UiState
import com.songlib.core.data.repos.PreferencesRepo
import com.songlib.core.ui.components.indicators.EmptyState
import com.songlib.core.ui.components.indicators.ErrorState
import com.songlib.feature.home.HomeViewModel
import com.songlib.core.ui.MainViewModel
import com.songlib.feature.home.components.HomeSkeleton
import kotlinx.coroutines.flow.collectLatest

@Composable
fun HomeScreen(
    navController: NavHostController,
    mainViewModel: MainViewModel,
    homeViewModel: HomeViewModel,
    prefsRepo: PreferencesRepo,
) {
    val uiState by homeViewModel.uiState.collectAsState()
    val songs   by homeViewModel.songs.collectAsState(initial = emptyList())
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        homeViewModel.toastEvent.collectLatest { msg ->
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(Unit) { homeViewModel.fetchData() }

    when (uiState) {
        is UiState.Error -> ErrorState(
            message = (uiState as UiState.Error).message,
            retryAction = { homeViewModel.fetchData() }
        )

        UiState.Loading -> HomeSkeleton()

        UiState.Filtered -> {
            if (songs.isEmpty()) {
                if (!prefsRepo.isDataLoaded) {
                    HomeSkeleton()
                } else {
                    EmptyState(
                        message = "It appears you didn't finish your songbook selection, " +
                                "that's why it's empty here at the moment.\n\nLet's fix that asap!",
                        messageIcon = Icons.Default.EditNote,
                        onAction = {
                            homeViewModel.clearData { success ->
                                if (success) {
                                    mainViewModel.reset()
                                }
                            }
                        }
                    )
                }
            } else {
                HomeContent(
                    viewModel = homeViewModel,
                    navController = navController,
                    prefsRepo = prefsRepo
                )
            }
        }

        else -> HomeSkeleton()
    }
}

