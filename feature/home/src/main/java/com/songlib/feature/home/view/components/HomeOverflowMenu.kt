package com.songlib.feature.home.view.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.songlib.core.common.utils.Routes

@Composable
fun HomeOverflowMenu(
    expanded: Boolean,
    onDismiss: () -> Unit,
    navController: NavHostController,
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismiss,
    ) {
        DropdownMenuItem(
            text = { Text("App Settings") },
            leadingIcon = { Icon(Icons.Default.Settings, null) },
            onClick = {
                onDismiss()
                navController.navigate(Routes.SETTINGS)
            }
        )
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
    }
}
