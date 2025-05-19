package com.songlib.presentation.screens.selection.step2

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.songlib.domain.entities.UiState
import com.songlib.presentation.components.*
import com.songlib.presentation.navigation.Routes
import com.songlib.presentation.viewmodels.SelectionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Step2Screen(
    viewModel: SelectionViewModel,
    navController: NavHostController,
) {
    var fetchData by rememberSaveable { mutableStateOf(0) }

    if (fetchData == 0) {
        viewModel.fetchSongs()
        fetchData++
    }

    val uiState by viewModel.uiState.collectAsState()

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
                        errorMessage = (uiState as UiState.Error).errorMessage,
                        onRetry = { viewModel.fetchSongs() }
                    )

                    is UiState.Loading -> LoadingState("Loading songs ...")
                    is UiState.Saving -> LoadingState("Saving songs ...")

                    is UiState.Loaded -> {
                        viewModel.saveSongs()
                    }

                    else -> EmptyState()
                }
            }
        },
    )
}