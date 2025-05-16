package com.songlib.presentation.screens.selection.step1

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.songlib.domain.entities.UiState
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
        topBar = { Step1TopBar(uiState = uiState) },
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
                    selectedBooks = viewModel.getSelectedBooks(),
                    onSaveConfirmed = { viewModel.saveBooks(it) }
                )
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Step1TopBar(uiState: UiState) {
    Surface(shadowElevation = 3.dp) {
        TopAppBar(
            title = { Text("Select Songbooks") },
            actions = {
                if (uiState != UiState.Loading && uiState != UiState.Saving) {
                    IconButton(onClick = { /* Implement Refresh logic if needed */ }) {
                        Icon(
                            imageVector = Icons.Filled.Refresh,
                            contentDescription = "Refresh"
                        )
                    }
                }
            }
        )
    }
}
