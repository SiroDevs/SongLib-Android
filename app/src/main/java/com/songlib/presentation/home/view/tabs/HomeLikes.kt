package com.songlib.presentation.home.view.tabs

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.navigation.NavHostController
import com.songlib.data.models.Song
import com.songlib.domain.entity.UiState
import com.songlib.presentation.components.action.AppTopBar
import com.songlib.presentation.components.indicators.*
import com.songlib.presentation.home.components.SongsList
import com.songlib.presentation.navigation.Routes
import com.songlib.presentation.home.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeLikes(
    viewModel: HomeViewModel, navController: NavHostController
) {
    val uiState by viewModel.uiState.collectAsState()
    val likes by viewModel.likes.collectAsState(initial = emptyList())
    var selectedSongs by remember { mutableStateOf<Set<Song>>(emptySet()) }

    Scaffold(
        topBar = {
            AppTopBar(
                title = if (selectedSongs.isEmpty()) "Liked Songs" else "${selectedSongs.size} selected",
                actions = {
                    if (selectedSongs.isEmpty()) {
                        IconButton(onClick = { navController.navigate(Routes.SETTINGS) }) {
                            Icon(Icons.Filled.Settings, contentDescription = "Settings")
                        }
                    } else {
                        IconButton(onClick = { viewModel.likeSongs(selectedSongs) }) {
                            Icon(Icons.Default.FavoriteBorder, contentDescription = "Like")
                        }
                        if (selectedSongs.size == 1) {
                            IconButton(onClick = {}) {
                                Icon(Icons.Default.Share, contentDescription = "Share")
                            }
                        }
                        IconButton(onClick = {}) {
                            Icon(Icons.Default.FormatListNumbered, contentDescription = "Listing")
                        }
                    }
                },
                showGoBack = selectedSongs.isNotEmpty(),
                onNavIconClick = { selectedSongs = emptySet() }
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
                            onSongSelected = { song ->
                                selectedSongs =
                                    if (selectedSongs.contains(song)) selectedSongs - song
                                    else selectedSongs + song
                            }
                        )
                    }

                else -> EmptyState()
            }
        }
    }
}
