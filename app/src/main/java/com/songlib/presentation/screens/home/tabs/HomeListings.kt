package com.songlib.presentation.screens.home.tabs

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.ExperimentalMaterial3Api
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
fun HomeListings(
    viewModel: HomeViewModel,
    navController: NavHostController
) {
    val uiState by viewModel.uiState.collectAsState()
    val listings by viewModel.likes.collectAsState(initial = emptyList())
    var selectedSongs by remember { mutableStateOf<Set<Song>>(emptySet()) }

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Songs Listings",
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
                    if (listings.isEmpty()) {
                        EmptyState(
                            message = "Start adding lists of songs,\\nif you don't want to see this again",
                            messageIcon = Icons.Default.FormatListNumbered
                        )
                    } else {
                        SongsList(
                            songs = listings,
                            viewModel = viewModel,
                            navController = navController,
                            selectedSongs = selectedSongs,
                            onSongSelected = { },
                        )
                    }

                else -> EmptyState()
            }
        }
    }
}
