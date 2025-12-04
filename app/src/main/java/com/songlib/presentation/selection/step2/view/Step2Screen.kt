package com.songlib.presentation.selection.step2.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.*
import androidx.navigation.NavHostController
import com.songlib.domain.entity.UiState
import com.songlib.presentation.components.indicators.*
import com.songlib.presentation.navigation.Routes
import com.songlib.presentation.selection.step2.Step2ViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Step2Screen(
    navController: NavHostController,
    viewModel: Step2ViewModel,
) {
    var fetchData by rememberSaveable { mutableIntStateOf(0) }

    if (fetchData == 0) {
        viewModel.fetchSongs()
        fetchData++
    }

    val uiState by viewModel.uiState.collectAsState()
    val progress by viewModel.progress.collectAsState(initial = 0)

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
                        retryAction = { viewModel.fetchSongs() }
                    )

                    is UiState.Loading -> LoadingState(
                        title = "Loading Songs ...",
                        fileName = "loading-hand",
                    )

                    is UiState.Saving ->
                        LoadingState(
                            title = "Saving your songs ...",
                            showProgress = true,
                            progressValue = progress,
                            fileName = "cloud-download",
                        )

                    is UiState.Loaded -> viewModel.saveSongs()
                    else -> EmptyState()
                }
            }
        },
    )
}
