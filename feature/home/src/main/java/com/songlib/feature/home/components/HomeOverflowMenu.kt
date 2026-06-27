package com.songlib.feature.home.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.songlib.core.common.utils.Routes

@Composable
fun HomeOverflowMenu(
    expanded: Boolean,
    onDismiss: () -> Unit,
    hasEdits: Boolean,
    isAdmin: Boolean,
    navController: NavHostController,
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismiss,
    ) {
        if (hasEdits) {
            DropdownMenuItem(
                text = { Text("My Edits") },
                leadingIcon = { Icon(Icons.Default.Checklist, null) },
                onClick = {
                    onDismiss()
                    navController.navigate(Routes.USER_EDITS)
                }
            )
        }
        if (isAdmin) {
            DropdownMenuItem(
                text = {
                    Text(
                        "Admin: Pending Edits",
                        color = MaterialTheme.colorScheme.primary,
                    )
                },
                leadingIcon = {
                    Icon(
                        Icons.Default.AdminPanelSettings,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                    )
                },
                onClick = {
                    onDismiss()
                    navController.navigate(Routes.ADMIN_EDITS)
                }
            )
        }
        DropdownMenuItem(
            text = { Text("Donate to SongLib") },
            leadingIcon = { Icon(Icons.Default.Info, null) },
            onClick = {
                onDismiss()
                navController.navigate(Routes.DONATION)
            }
        )
        DropdownMenuItem(
            text = { Text("How It Works") },
            leadingIcon = { Icon(Icons.Default.Info, null) },
            onClick = {
                onDismiss()
                navController.navigate(Routes.HOW_IT_WORKS)
            }
        )
        DropdownMenuItem(
            text = { Text("Help & Feedback") },
            leadingIcon = { Icon(Icons.Default.HelpOutline, null) },
            onClick = { onDismiss(); navController.navigate(Routes.HELP) }
        )
        DropdownMenuItem(
            text = { Text("Settings") },
            leadingIcon = { Icon(Icons.Default.Settings, null) },
            onClick = {
                onDismiss()
                navController.navigate(Routes.SETTINGS)
            }
        )
    }
}