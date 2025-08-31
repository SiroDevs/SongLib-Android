package com.songlib.presentation.screens.home.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import com.songlib.data.models.Song
import com.songlib.presentation.components.action.AppTopBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeSearchAppBar(
    selectedSongs: Set<Song>,
    onSearchClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onLikeClick: () -> Unit,
    onShareClick: () -> Unit,
    onClearSelection: () -> Unit,
) {
    AppTopBar(
        title = if (selectedSongs.isEmpty()) "SongLib" else "${selectedSongs.size} selected",
        actions = {
            if (selectedSongs.isEmpty()) {
                IconButton(onClick = onSearchClick) {
                    Icon(Icons.Filled.Search, contentDescription = "Search")
                }
                IconButton(onClick = onSettingsClick) {
                    Icon(Icons.Filled.Settings, contentDescription = "Settings")
                }
            } else {
                IconButton(onClick = onLikeClick) {
                    Icon(Icons.Default.FavoriteBorder, contentDescription = "Like")
                }
                if (selectedSongs.size == 1) {
                    IconButton(onClick = onShareClick) {
                        Icon(Icons.Default.Share, contentDescription = "Share")
                    }
                }
                IconButton(onClick = onLikeClick) {
                    Icon(Icons.Default.FormatListNumbered, contentDescription = "Listing")
                }
            }
        },
        showGoBack = selectedSongs.isNotEmpty(),
        onNavIconClick = onClearSelection
    )
}
