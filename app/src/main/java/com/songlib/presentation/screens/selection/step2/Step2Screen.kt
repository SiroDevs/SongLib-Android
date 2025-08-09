package com.songlib.presentation.screens.selection.step2

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.songlib.domain.entity.UiState
import com.songlib.presentation.components.indicators.*
import com.songlib.presentation.navigation.Routes
import com.songlib.presentation.viewmodels.SelectionViewModel
import com.swahilib.presentation.components.indicators.EmptyState
import com.swahilib.presentation.components.indicators.ErrorState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Step2Screen(
    viewModel: SelectionViewModel,
    navController: NavHostController,
) {
    var fetchData by rememberSaveable { mutableIntStateOf(0) }

    if (fetchData == 0) {
        viewModel.fetchSongs()
        fetchData++
    }

    val uiState by viewModel.uiState.collectAsState()
    val progress by viewModel.progress.collectAsState(initial = 0)
    val status by viewModel.status.collectAsState(initial = "Saving songs ...")

    LaunchedEffect(uiState) {
        if (uiState == UiState.Saved) {
            navController.navigate(Routes.HOME)
        }
    }

    Scaffold(
        content = { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(color = MaterialTheme.colorScheme.surface)
            ) {
                when (uiState) {
                    is UiState.Error -> ErrorState(
                        message = (uiState as UiState.Error).message,
                        onRetry = { viewModel.fetchSongs() }
                    )

                    is UiState.Loading -> LoadingState(
                        title = "Inapakia data ...",
                        fileName = "bar-loader",
                    )

                    is UiState.Saving ->
                        LoadingState(
                            title = status,
                            fileName = "opener-loading",
                            showProgress = true,
                            progressValue = progress
                        )

                    is UiState.Loaded -> {
                        viewModel.saveSongs()
                    }

                    else -> EmptyState()
                }
            }
        },
    )
}