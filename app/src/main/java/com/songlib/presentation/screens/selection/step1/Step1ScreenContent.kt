package com.songlib.presentation.screens.selection.step1

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.songlib.data.models.Book
import com.songlib.domain.entities.*
import com.songlib.presentation.components.*
import com.songlib.presentation.components.listitems.BookItem


@Composable
fun Step1Content(
    uiState: UiState,
    books: List<Selectable<Book>>,
    onRetry: () -> Unit,
    onBookClick: (Selectable<Book>) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.surface)
    ) {
        when (uiState) {
            is UiState.Error -> ErrorState(
                errorMessage = uiState.errorMessage,
                onRetry = onRetry
            )
            is UiState.Loading -> LoadingState("Loading books ...")
            is UiState.Saving -> LoadingState("Saving books ...")
            is UiState.Loaded -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(5.dp),
                    contentPadding = PaddingValues(horizontal = 5.dp)
                ) {
                    items(books) { book ->
                        BookItem(
                            item = book,
                            onClick = { onBookClick(book) }
                        )
                    }
                }
            }
            else -> EmptyState()
        }
    }
}

@Composable
fun Step1Fab(
    selectedBooks: List<Book>,
    onSaveConfirmed: (List<Book>) -> Unit
) {
    var showConfirmDialog by remember { mutableStateOf(false) }
    var showNoSelectionDialog by remember { mutableStateOf(false) }

    if (showConfirmDialog) {
        ConfirmSaveDialog(
            onConfirm = {
                onSaveConfirmed(selectedBooks)
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
            if (selectedBooks.isNotEmpty()) {
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
