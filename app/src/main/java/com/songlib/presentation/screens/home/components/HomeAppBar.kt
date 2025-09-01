package com.songlib.presentation.screens.home.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import com.songlib.data.models.Song
import com.songlib.presentation.components.action.AppTopBar
import com.songlib.presentation.components.general.QuickFormDialog
import com.songlib.presentation.viewmodels.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeSearchAppBar(
    viewModel: HomeViewModel,
    selectedSongs: Set<Song>,
    onSearchClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onShareClick: () -> Unit,
    onClearSelection: () -> Unit,
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var showListingSheet by remember { mutableStateOf(false) }

    if (showAddDialog) {
        QuickFormDialog(
            title = "New Listing",
            label = "Listing title",
            onDismiss = { showAddDialog = false },
            onConfirm = { title ->
                viewModel.saveListing(title)
                showAddDialog = false
            }
        )
    }

    if (showListingSheet) {
        val listings by viewModel.listings.collectAsState(initial = emptyList())
        ChoosingListingSheet(
            listings = listings,
            onDismiss = { showListingSheet = false },
            onNewListClick = {

            },
            onListingClick = { listing ->
                viewModel.saveListItems(listing, selectedSongs)
                showListingSheet = false
                onClearSelection
            },
            onDone = { showListingSheet = false }
        )
    }

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
                IconButton(
                    onClick = { viewModel.likeSongs(selectedSongs) }
                ) {
                    Icon(Icons.Default.FavoriteBorder, contentDescription = "Like")
                }
                if (selectedSongs.size == 1) {
                    IconButton(onClick = onShareClick) {
                        Icon(Icons.Default.Share, contentDescription = "Share")
                    }
                }
                IconButton(
                    onClick = { showListingSheet = true }
                ) {
                    Icon(Icons.Default.FormatListNumbered, contentDescription = "Listing")
                }
            }
        },
        showGoBack = selectedSongs.isNotEmpty(),
        onNavIconClick = onClearSelection
    )
}

