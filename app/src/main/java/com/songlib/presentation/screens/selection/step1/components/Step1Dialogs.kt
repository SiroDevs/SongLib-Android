package com.songlib.presentation.screens.selection.step1.components

import androidx.compose.material3.*
import androidx.compose.runtime.Composable

@Composable
fun ConfirmSaveDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("OKAY")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        title = { Text("Save Selection") },
        text = { Text("Are you sure you want to save the selected books?") }
    )
}

@Composable
fun NoSelectionDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("OKAY")
            }
        },
        title = { Text("No Selection") },
        text = { Text("Please select at least one book before saving.") }
    )
}

