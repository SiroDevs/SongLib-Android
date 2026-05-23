package com.songlib.feature.home.view.tabs

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Brightness6
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.FormatListNumbered
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.songlib.core.database.model.SongEntity
import com.songlib.core.common.entity.UiState
import com.songlib.core.ui.components.action.AppTopBar
import com.songlib.feature.home.components.SongsList
import com.songlib.core.common.utils.Routes
import com.songlib.core.ui.components.indicators.EmptyState
import com.songlib.feature.home.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeLikes(
    viewModel: HomeViewModel,
    navController: NavHostController,
    onShowThemeDialog: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()
    val searchQry by viewModel.searchQuery.collectAsState()
    val likes by viewModel.likes.collectAsState(initial = emptyList())
    var selectedSongs by remember { mutableStateOf<Set<SongEntity>>(emptySet()) }

    LaunchedEffect(likes) {
        selectedSongs = selectedSongs.filter { likes.contains(it) }.toSet()
    }

    Scaffold(
        topBar = {
            AppTopBar(
                title = if (selectedSongs.isEmpty()) "Liked Songs" else "${selectedSongs.size} selected",
                actions = {
                    if (selectedSongs.isEmpty()) {
                        IconButton(onClick = onShowThemeDialog ) {
                            Icon(
                                imageVector = Icons.Filled.Brightness6, contentDescription = ""
                            )
                        }
                        IconButton(onClick = { navController.navigate(Routes.SETTINGS) }) {
                            Icon(Icons.Filled.Settings, contentDescription = "Settings")
                        }
                    } else {
                        val allLiked = selectedSongs.all { it.liked }
                        IconButton(onClick = {
                            viewModel.likeSongs(selectedSongs)
                            selectedSongs = emptySet()
                        }) {
                            Icon(
                                if (allLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = if (allLiked) "Unlike" else "Like",
                                tint = if (allLiked) MaterialTheme.colorScheme.primary
                                       else LocalContentColor.current
                            )
                        }
                        if (selectedSongs.size == 1) {
                            IconButton(onClick = {
                                val song = selectedSongs.first()
                            }) {
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
                .padding(innerPadding)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            when (uiState) {
                is UiState.Filtered, UiState.Saving ->
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
                            showSearch = false,
                            showBookFilter = false,
                            onQueryChange = {},
                            searchQuery = searchQry,
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
