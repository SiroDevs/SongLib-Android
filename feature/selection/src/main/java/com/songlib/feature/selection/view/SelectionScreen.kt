package com.songlib.feature.selection.view

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Brightness6
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.songlib.core.common.entity.UiState
import com.songlib.core.ui.components.action.AppTopBar
import com.songlib.core.common.utils.Routes
import com.songlib.core.data.repos.ThemeRepo
import com.songlib.core.designsystem.theme.ThemeSelectorDialog
import com.songlib.core.ui.components.indicators.EmptyState
import com.songlib.core.ui.components.indicators.ErrorState
import com.songlib.core.ui.components.indicators.LoadingState
import com.songlib.feature.selection.SelectionViewModel
import com.songlib.feature.selection.components.Step1Fab

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectionScreen(
    navController: NavHostController,
    viewModel: SelectionViewModel,
    themeRepo: ThemeRepo
) {
    var fetchData by rememberSaveable { mutableIntStateOf(0) }
    var showThemeDialog by remember { mutableStateOf(false) }

    if (fetchData == 0) {
        viewModel.fetchBooks()
        fetchData++
    }

    val books by viewModel.books.collectAsState(initial = emptyList())
    val uiState by viewModel.uiState.collectAsState()
    val theme = themeRepo.selectedTheme

    LaunchedEffect(uiState) {
        if (uiState == UiState.Saved) {
            navController.navigate(Routes.HOME)
        }
    }

    if (showThemeDialog) {
        ThemeSelectorDialog(
            current = theme,
            onDismiss = { showThemeDialog = false },
            onThemeSelected = {
                themeRepo.setTheme(it)
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
                                imageVector = Icons.Filled.Refresh, contentDescription = "",
                            )
                        }
                    }

                    IconButton(onClick = { showThemeDialog = true }) {
                        Icon(
                            imageVector = Icons.Filled.Brightness6, contentDescription = ""
                        )
                    }
                }
            )
        },
        content = { paddingValues ->
            when (uiState) {
                is UiState.Error -> ErrorState(
                    message = (uiState as UiState.Error).message,
                    retryAction = { viewModel.fetchBooks() }
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
                    SelectionContent(
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