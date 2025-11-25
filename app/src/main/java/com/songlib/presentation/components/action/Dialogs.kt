package com.songlib.presentation.components.action

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable

@Composable
fun ProLimitDialog(
    onDismiss: () -> Unit,
    onUpgrade: () -> Unit,
    title: String = "Support us by upgrading",
    message: String = "Please purchase a subscription if you want to continue using this feature and all other Pro features.",
    upgradeButtonText: String = "Upgrade",
    dismissButtonText: String = "Not Now"
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = { Text(message) },
        confirmButton = {
            TextButton(onClick = onUpgrade) {
                Text(upgradeButtonText)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(dismissButtonText)
            }
        }
    )
}