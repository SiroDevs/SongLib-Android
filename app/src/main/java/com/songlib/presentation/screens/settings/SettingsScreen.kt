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
import com.songlib.domain.repository.ThemeRepository
import com.songlib.domain.repository.ThemeSelectorDialog
import com.songlib.domain.repository.appThemeName
import com.songlib.presentation.components.action.AppTopBar
import com.songlib.presentation.theme.*

@Composable
fun SettingsScreen(
    navController: NavHostController,
    themeRepo: ThemeRepository,
) {
    var showThemeDialog by remember { mutableStateOf(false) }
    val theme = themeRepo.selectedTheme

    Scaffold(
        topBar = {
            AppTopBar(
                title = "App Settings",
                showGoBack = true,
                onNavIconClick = { navController.popBackStack() }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            ListItem(
                headlineContent = { Text("App Theme") },
                modifier = Modifier.clickable { showThemeDialog = true },
                supportingContent = {
                    Text(appThemeName(theme))
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
                    themeRepo.setTheme(it)
                    showThemeDialog = false
                }
            )
        }
    }
}