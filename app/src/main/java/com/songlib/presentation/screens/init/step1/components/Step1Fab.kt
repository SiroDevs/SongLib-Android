package com.songlib.presentation.screens.init.step1.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import com.songlib.data.models.Book
import com.songlib.presentation.viewmodels.Step1ViewModel

@Composable
fun Step1Fab(
    viewModel: Step1ViewModel,
    onSaveConfirmed: (List<Book>) -> Unit
) {
    var showConfirmDialog by remember { mutableStateOf(false) }
    var showNoSelectionDialog by remember { mutableStateOf(false) }

    if (showConfirmDialog) {
        ConfirmSaveDialog(
            onDismiss = { showConfirmDialog = false },
            onConfirm = {
                onSaveConfirmed(viewModel.getSelectedBookList())
                showConfirmDialog = false
            }
        )
    }

    if (showNoSelectionDialog) {
        NoSelectionDialog(
            onDismiss = { showNoSelectionDialog = false }
        )
    }

    ExtendedFloatingActionButton(
        onClick = {
            if (viewModel.getSelectedBookList().isNotEmpty()) {
                showConfirmDialog = true
            } else {
                showNoSelectionDialog = true
            }
        },
        icon = { Icon(Icons.Filled.Check, "Proceed") },
        text = { Text(text = "Proceed") },
    )
}
