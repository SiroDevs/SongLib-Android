package com.songlib.presentation.screens.selection

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.songlib.domain.entities.UiState
import com.songlib.presentation.components.*
import com.songlib.presentation.components.listitems.BookItem
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
        fetchData = fetchData.inc()
    }

    val books by viewModel.books.collectAsState(initial = emptyList())
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            Surface(shadowElevation = 3.dp) {
                TopAppBar(
                    title = { Text("Select Songbooks") },
                    actions = {
                        IconButton(onClick = { }) {
                            Icon(
                                imageVector = Icons.Filled.Refresh,
                                contentDescription = "Refresh"
                            )
                        }
                    },
                )
            }
        },
        content = {
            Box(
                modifier = Modifier
                    .padding(it)
                    .fillMaxSize()
                    .background(color = MaterialTheme.colorScheme.surface)
            ) {
                when (val state = uiState) {
                    is UiState.Error -> ErrorState(
                        errorMessage = state.errorMessage,
                        onRetry = { viewModel.fetchBooks() }
                    )
                    is UiState.Loading -> LoadingState("Loading books ...")
                    is UiState.Saving -> LoadingState("Saving books ...")
                    is UiState.Loaded -> {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp)
                        ) {
                            items(books) { book ->
                                BookItem(
                                    item = book,
                                    //onActivityClick = { clickedItem -> onItemClick(clickedItem) },
                                )
                            }
                        }
                    }
                    else -> EmptyState()
                }
            }
        }
    )
}