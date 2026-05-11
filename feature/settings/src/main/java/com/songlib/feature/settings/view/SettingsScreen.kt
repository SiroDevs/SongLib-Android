package com.songlib.presentation.settings.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.songlib.domain.repos.ThemeRepository
import com.songlib.domain.repos.ThemeSelectorDialog
import com.songlib.domain.repos.appThemeName
import com.songlib.presentation.components.action.AppTopBar
import com.songlib.presentation.navigation.Routes
import com.songlib.presentation.settings.SettingsViewModel
import com.songlib.presentation.settings.components.ConfirmResetDialog
import com.songlib.presentation.settings.components.SettingsSectionTitle

@Composable
fun SettingsScreen(
    navController: NavHostController,
    viewModel: SettingsViewModel,
    themeRepo: ThemeRepository,
) {
    val theme = themeRepo.selectedTheme
    var showThemeDialog by remember { mutableStateOf(false) }
    var showResetDialog by remember { mutableStateOf(false) }

    fun navigateToSplash() {
        navController.navigate(Routes.SPLASH) {
            popUpTo(0) { inclusive = true }
            launchSingleTop = true
        }
    }

    if (showResetDialog) {
        ConfirmResetDialog(
            onDismiss = { showResetDialog = false },
            onConfirm = {
                showResetDialog = false
                viewModel.clearData()
                navigateToSplash()
            }
        )
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
            SettingsSectionTitle("Slides")
            ListItem(
                leadingContent = {
                    Icon(
                        Icons.Default.Swipe, contentDescription = "slides"
                    )
                },
                headlineContent = { Text("Song Slides") },
                supportingContent = { Text("Swipe verses horizontally") },
                trailingContent = {
                    Switch(
                        checked = viewModel.horizontalSlides,
                        onCheckedChange = {
                            viewModel.updateHorizontalSlides(it)
                        }
                    )
                }
            )
            HorizontalDivider()

            SettingsSectionTitle("Display")
            ListItem(
                leadingContent = {
                    Icon(
                        Icons.Default.Brightness6, contentDescription = "Theme"
                    )
                },
                headlineContent = { Text("App Theme") },
                supportingContent = { Text(appThemeName(theme)) },
                modifier = Modifier.clickable { showThemeDialog = true },
            )
            HorizontalDivider()

            SettingsSectionTitle("Selection")
            ListItem(
                leadingContent = {
                    Icon(
                        Icons.Default.EditNote, contentDescription = "Reset"
                    )
                },
                headlineContent = { Text("Modify Collection") },
                supportingContent = { Text("Add or Remove Songbooks") },
                modifier = Modifier.clickable {
                    viewModel.updateSelection(true)
                    navigateToSplash()
                },
            )
            ListItem(
                leadingContent = {
                    Icon(
                        Icons.Default.Refresh, contentDescription = "Reset"
                    )
                },
                headlineContent = { Text("Select Afresh") },
                supportingContent = { Text("Reset everything and start over") },
                modifier = Modifier.clickable { showResetDialog = true },
            )
            HorizontalDivider()
        }
    }
}
