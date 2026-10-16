package com.songlib.feature.selection.view.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import com.songlib.core.common.utils.AppLimits
import com.songlib.core.database.model.BookEntity
import com.songlib.core.ui.components.general.*
import com.songlib.feature.selection.viewmodel.SelectionViewModel

@Composable
fun Step1Fab(
    viewModel: SelectionViewModel,
    selectedCount: Int,
    onSaveConfirmed: (List<BookEntity>) -> Unit
) {
    var showConfirmDialog by remember { mutableStateOf(false) }
    var showNoSelectionDialog by remember { mutableStateOf(false) }
    var showTooManyDialog by remember { mutableStateOf(false) }

    if (showConfirmDialog) {
        ConfirmDialog(
            title = "Save Selection",
            message = "Are you sure you want to save the selected books?",
            onDismiss = { showConfirmDialog = false },
            onConfirm = {
                onSaveConfirmed(viewModel.getSelectedBookList())
                showConfirmDialog = false
            }
        )
    }

    if (showNoSelectionDialog) {
        InfoDialog(
            title = "No Selection",
            message = "Please select at least one book before saving.",
            onDismiss = { showNoSelectionDialog = false }
        )
    }

    if (showTooManyDialog) {
        InfoDialog(
            title = "Too Many Songbooks",
            message = "You can keep up to ${AppLimits.MAX_SONGBOOKS} songbooks. " +
                "Please deselect ${selectedCount - AppLimits.MAX_SONGBOOKS} to continue.",
            onDismiss = { showTooManyDialog = false }
        )
    }

    ExtendedFloatingActionButton(
        onClick = {
            when {
                selectedCount == 0 -> showNoSelectionDialog = true
                selectedCount > AppLimits.MAX_SONGBOOKS -> showTooManyDialog = true
                else -> showConfirmDialog = true
            }
        },
        icon = { Icon(Icons.Filled.Check, "Proceed") },
        text = { Text(text = "Proceed ($selectedCount/${AppLimits.MAX_SONGBOOKS})") },
    )
}
