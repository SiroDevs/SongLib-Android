package com.songlib.presentation.screens.settings

import androidx.compose.material3.*
import androidx.compose.runtime.Composable

@Composable
fun ConfirmResetDialog(
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
        title = { Text("Reset Your Selection") },
        text = { Text("Are you sure you want to reset everything and start over?") }
    )
}
