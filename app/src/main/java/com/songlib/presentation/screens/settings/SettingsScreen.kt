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
import com.songlib.domain.repository.*
import com.songlib.domain.repository.appThemeName
import com.songlib.presentation.components.action.AppTopBar

@Composable
fun SettingsScreen(
    navController: NavHostController,
    themeRepo: ThemeRepository,
    prefs: PrefsRepository = hiltViewModel() // Inject PrefsRepository here
) {
    var showThemeDialog by remember { mutableStateOf(false) }
    val theme = themeRepo.selectedTheme

    // Read and store switch state
    var horizontalSlides by remember { mutableStateOf(prefs.horizontalSlides) }

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
            // Theme selector
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

            // Horizontal slides toggle
            ListItem(
                headlineContent = { Text("Horizontal Slides") },
                supportingContent = { Text("Swipe songs horizontally instead of vertically") },
                trailingContent = {
                    Switch(
                        checked = horizontalSlides,
                        onCheckedChange = { isChecked ->
                            horizontalSlides = isChecked
                            prefs.horizontalSlides = isChecked // Persist change
                        }
                    )
                },
                leadingContent = {
                    Icon(Icons.Default.Swipe, contentDescription = "Horizontal slides")
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
    }
}

