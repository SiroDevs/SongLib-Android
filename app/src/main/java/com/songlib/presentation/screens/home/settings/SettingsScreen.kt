package com.songlib.presentation.screens.home.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.songlib.presentation.components.action.AppTopBar

@Composable
fun SettingsScreen(
    navController: NavHostController
) {
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
                .padding(16.dp)
        ) {
            Text("Settings Page", style = MaterialTheme.typography.h3)
        }
    }
}
