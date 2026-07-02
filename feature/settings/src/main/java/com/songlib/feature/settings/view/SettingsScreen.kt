package com.songlib.feature.settings.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.songlib.core.common.utils.Routes
import com.songlib.core.data.repos.ThemeRepo
import com.songlib.core.data.repos.appThemeName
import com.songlib.core.designsystem.theme.ThemeSelectorDialog
import com.songlib.core.ui.MainViewModel
import com.songlib.core.ui.components.action.AppTopBar
import com.songlib.feature.settings.SettingsViewModel
import com.songlib.feature.settings.components.ConfirmResetDialog
import com.songlib.feature.settings.components.SettingsSectionTitle

@Composable
fun SettingsScreen(
    navController: NavHostController,
    mainViewModel: MainViewModel,
    settViewModel: SettingsViewModel,
    themeRepo: ThemeRepo,
) {
    val theme = themeRepo.selectedTheme
    var showThemeDialog by remember { mutableStateOf(false) }
    var showResetDialog by remember { mutableStateOf(false) }

    if (showResetDialog) {
        ConfirmResetDialog(
            onDismiss = { showResetDialog = false },
            onConfirm = {
                showResetDialog = false
                settViewModel.clearData { success ->
                    if (success) {
                        mainViewModel.reset()
                    }
                }
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
            SettingsSectionTitle("Account")
            ListItem(
                leadingContent = { Icon(Icons.Default.AccountCircle, "Profile") },
                headlineContent = { Text("Your Profile") },
                supportingContent = { Text("Manage your Profile") },
                modifier = Modifier.clickable {
                    navController.navigate(Routes.USER_PROFILE)
                }
            )
            HorizontalDivider()

            SettingsSectionTitle("Slides")
            ListItem(
                leadingContent = { Icon(Icons.Default.Swipe, "slides") },
                headlineContent = { Text("Song Slides") },
                supportingContent = { Text("Swipe verses horizontally") },
                trailingContent = {
                    Switch(
                        checked = settViewModel.horizontalSlides,
                        onCheckedChange = { settViewModel.updateHorizontalSlides(it) }
                    )
                }
            )
            HorizontalDivider()

            SettingsSectionTitle("Demo")
            ListItem(
                leadingContent = { Icon(Icons.Default.PlayCircleOutline, "Demo Mode") },
                headlineContent = { Text("Demo Mode") },
                supportingContent = { Text("Show guided tour on home screen") },
                trailingContent = {
                    Switch(
                        checked = settViewModel.demoMode,
                        onCheckedChange = { settViewModel.updateDemoMode(it) }
                    )
                }
            )
            HorizontalDivider()

            SettingsSectionTitle("Display")
            ListItem(
                leadingContent = { Icon(Icons.Default.Brightness6, "Theme") },
                headlineContent = { Text("App Theme") },
                supportingContent = { Text(appThemeName(theme)) },
                modifier = Modifier.clickable { showThemeDialog = true }
            )
            HorizontalDivider()

            SettingsSectionTitle("Broadcast")
            ListItem(
                leadingContent = { Icon(Icons.Default.Wifi, "Broadcast to PC") },
                headlineContent = { Text("Broadcast to PC") },
                supportingContent = { Text("Mirror your presenter screen over hotspot or Wi-Fi") },
                modifier = Modifier.clickable { navController.navigate(Routes.BROADCAST) }
            )
            HorizontalDivider()

            SettingsSectionTitle("Donate to SongLib")
            ListItem(
                leadingContent = {
                    Icon(
                        Icons.Default.VolunteerActivism,
                        contentDescription = null
                    )
                },
                headlineContent = { Text("Donate Now") },
                supportingContent = { Text("We need your support to continue serving you") },
                modifier = Modifier.clickable { navController.navigate(Routes.DONATION) },
            )
            HorizontalDivider()

            SettingsSectionTitle("Selection")
            ListItem(
                leadingContent = { Icon(Icons.Default.EditNote, "Reset") },
                headlineContent = { Text("Modify Collection") },
                supportingContent = { Text("Add or Remove Songbooks") },
                modifier = Modifier.clickable {
                    settViewModel.updateSelection(true)
                    navController.navigate(Routes.SELECTION) {
                        popUpTo(0) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
            ListItem(
                leadingContent = { Icon(Icons.Default.Refresh, "Reset") },
                headlineContent = { Text("Select Afresh") },
                supportingContent = { Text("Reset everything and start over") },
                modifier = Modifier.clickable { showResetDialog = true }
            )
            HorizontalDivider()
        }
    }
}
