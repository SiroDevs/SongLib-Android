package com.songlib.presentation.screens.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.songlib.presentation.components.action.AppTopBar
import com.songlib.presentation.theme.ThemeManager
import com.songlib.presentation.theme.ThemeSelectorDialog

@Composable
fun SettingsScreen(
    navController: NavHostController,
    themeManager: ThemeManager,
) {
    var showThemeDialog by remember { mutableStateOf(false) }
    val theme = themeManager.selectedTheme

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Settings",
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            ListItem(
                headlineContent = { Text("Theme") },
                modifier = Modifier.clickable { showThemeDialog = true },
                supportingContent = {
                    Text(
                        theme.name.lowercase().replaceFirstChar { it.uppercase() })
                },
                leadingContent = {
                    Icon(Icons.Default.Brightness6, contentDescription = "Theme")
                },
            )

            HorizontalDivider()
        }

        if (showThemeDialog) {
            ThemeSelectorDialog(
                current = theme,
                onDismiss = { showThemeDialog = false },
                onThemeSelected = {
                    themeManager.setTheme(it)
                    showThemeDialog = false
                }
            )
        }
    }
}