package com.songlib.presentation.screens.home.tabs

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.navigation.NavHostController
import com.songlib.data.models.Song
import com.songlib.domain.entity.UiState
import com.songlib.presentation.components.action.*
import com.songlib.presentation.components.indicators.*
import com.songlib.presentation.navigation.Routes
import com.songlib.presentation.screens.home.components.SongsList
import com.songlib.presentation.viewmodels.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeLikes(viewModel: HomeViewModel, navController: NavHostController) {
    val uiState by viewModel.uiState.collectAsState()
    val likes by viewModel.likes.collectAsState(initial = emptyList())
    var selectedSongs by remember { mutableStateOf<Set<Song>>(emptySet()) }

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Liked Songs",
                actions = {
                    IconButton(onClick = { navController.navigate(Routes.SETTINGS) }) {
                        Icon(Icons.Filled.Settings, contentDescription = "")
                    }
                }
            )
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            when (uiState) {
                is UiState.Filtered ->
                    if (likes.isEmpty()) {
                        EmptyState(
                            message = "Start liking songs when you view them,\n If you don't want to see this again",
                            messageIcon = Icons.Default.FavoriteBorder
                        )
                    } else {
                        SongsList(
                            songs = likes,
                            viewModel = viewModel,
                            navController = navController,
                            selectedSongs = selectedSongs,
                            onSongSelected = {},
                        )
                    }
                else -> EmptyState()
            }
        }
    }
}
