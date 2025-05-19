package com.songlib.presentation.screens.selection.step1

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.songlib.data.models.Book
import com.songlib.domain.entities.UiState
import com.songlib.presentation.components.action.AppTopBar
import com.songlib.presentation.navigation.Routes
import com.songlib.presentation.viewmodels.SelectionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Step1Screen(
    viewModel: SelectionViewModel,
    navController: NavHostController,
) {
    var fetchData by rememberSaveable { mutableStateOf(0) }

    if (fetchData == 0) {
        viewModel.fetchBooks()
        fetchData++
    }

    val books by viewModel.books.collectAsState(initial = emptyList())
    val uiState by viewModel.uiState.collectAsState()

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
                                contentDescription = "Refresh"
                            )
                        }
                    }
                }
            )
        },
        content = { paddingValues ->
            Step1Content(
                uiState = uiState,
                books = books,
                onRetry = { viewModel.fetchBooks() },
                onBookClick = { viewModel.toggleBookSelection(it) },
                modifier = Modifier.padding(paddingValues)
            )
        },
        floatingActionButton = {
            if (uiState == UiState.Loaded) {
                Step1Fab(
                    viewModel = viewModel,
                    onSaveConfirmed = {
                        viewModel.saveBooks(it)
                        navController.navigate(Routes.STEP_2)
                    }
                )
            }
        }
    )
}

@Composable
fun Step1Fab(
    viewModel: SelectionViewModel,
    onSaveConfirmed: (List<Book>) -> Unit
) {
    var showConfirmDialog by remember { mutableStateOf(false) }
    var showNoSelectionDialog by remember { mutableStateOf(false) }

    if (showConfirmDialog) {
        ConfirmSaveDialog(
            onConfirm = {
                onSaveConfirmed(viewModel.getSelectedBooks())
                showConfirmDialog = false
            },
            onDismiss = { showConfirmDialog = false }
        )
    }

    if (showNoSelectionDialog) {
        NoSelectionDialog(
            onDismiss = { showNoSelectionDialog = false }
        )
    }

    FloatingActionButton(
        onClick = {
            if (viewModel.getSelectedBooks().isNotEmpty()) {
                showConfirmDialog = true
            } else {
                showNoSelectionDialog = true
            }
        },
        containerColor = MaterialTheme.colorScheme.primary
    ) {
        Icon(Icons.Default.Check, contentDescription = "Save")
    }
}
