package com.songlib.presentation.screens.selection.step1

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.songlib.domain.entity.UiState
import com.songlib.domain.repository.ThemeManager
import com.songlib.domain.repository.ThemeSelectorDialog
import com.songlib.presentation.components.*
import com.songlib.presentation.components.action.AppTopBar
import com.songlib.presentation.components.indicators.LoadingState
import com.songlib.presentation.navigation.Routes
import com.songlib.presentation.screens.selection.step1.components.*
import com.songlib.presentation.theme.*
import com.songlib.presentation.viewmodels.SelectionViewModel
import com.swahilib.presentation.components.indicators.EmptyState
import com.swahilib.presentation.components.indicators.ErrorState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Step1Screen(
    viewModel: SelectionViewModel,
    navController: NavHostController,
    themeManager: ThemeManager
) {
    var fetchData by rememberSaveable { mutableStateOf(0) }
    var showThemeDialog by remember { mutableStateOf(false) }

    if (fetchData == 0) {
        viewModel.fetchBooks()
        fetchData++
    }

    val books by viewModel.books.collectAsState(initial = emptyList())
    val uiState by viewModel.uiState.collectAsState()
    val theme = themeManager.selectedTheme

    LaunchedEffect(uiState) {
        if (uiState == UiState.Saved) {
            navController.navigate(Routes.STEP_2)
        }
    }

    if (showThemeDialog) {
        ThemeSelectorDialog(
            current = theme,
            onDismiss = { showThemeDialog = false },
            onThemeSelected = {
                themeManager.setTheme(it)
                showThemeDialog = false
            }
        )
    }

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Select Songbooks",
                actions = {
                    if (uiState != UiState.Loading && uiState != UiState.Saving) {
                        IconButton(
                            onClick = { viewModel.fetchBooks() }
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Refresh,
                                contentDescription = "Refresh",
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }

                    IconButton(onClick = { showThemeDialog = true }) {
                        Icon(
                            imageVector = Icons.Filled.Brightness6,
                            contentDescription = "Change Theme"
                        )
                    }
                }
            )
        },
        content = { paddingValues ->
            when (uiState) {
                is UiState.Error -> ErrorState(
                    message = (uiState as UiState.Error).message,
                    onRetry = { viewModel.fetchBooks() }
                )

                is UiState.Loading -> LoadingState(
                    title = "Loading books ...",
                    fileName = "loading-hand"
                )

                is UiState.Saving ->
                    LoadingState(
                        title = "Saving books ...",
                        fileName = "cloud-download"
                    )

                is UiState.Loaded -> {
                    Step1Content(
                        books = books,
                        onBookClick = { viewModel.toggleBookSelection(it) },
                        modifier = Modifier.padding(paddingValues)
                    )
                }

                else -> EmptyState()
            }
        },
        floatingActionButton = {
            if (uiState == UiState.Loaded) {
                Step1Fab(
                    viewModel = viewModel,
                    onSaveConfirmed = { viewModel.saveSelectedBooks() }
                )
            }
        }
    )
}
