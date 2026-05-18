package com.songlib.feature.home.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.FormatListNumbered
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.songlib.core.database.model.SongEntity
import com.songlib.core.ui.components.action.AppTopBar
import com.songlib.core.ui.components.general.QuickFormDialog
import com.songlib.feature.home.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeSearchAppBar(
    viewModel: HomeViewModel,
    selectedSongs: Set<SongEntity>,
    onSearchClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onShareClick: () -> Unit,
    onClearSelection: () -> Unit,
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var showListingSheet by remember { mutableStateOf(false) }
    val listings by viewModel.listings.collectAsState(initial = emptyList())

    if (showAddDialog) {
        QuickFormDialog(
            title = "New ListingEntity",
            label = "ListingEntity title",
            onDismiss = { showAddDialog = false },
            onConfirm = { title ->
                if (viewModel.checkAndHandleNewListing()) {
                    viewModel.saveListing(title)
                    showAddDialog = false
                }
            }
        )
    }

    if (showListingSheet) {
        ChoosingListingSheet(
            listings = listings,
            onDismiss = { showListingSheet = false },
            onNewListClick = {
                showListingSheet = false
                if (viewModel.checkAndHandleNewListing()) {
                    showAddDialog = true
                }
            },
            onListingClick = { listing ->
                viewModel.saveListItems(listing, selectedSongs)
                showListingSheet = false
                onClearSelection()
            },
            onDone = { showListingSheet = false }
        )
    }

    AppTopBar(
        title = "SongLib",
        actions = {
            if (selectedSongs.isEmpty()) {
                IconButton(onClick = onSearchClick) {
                    Icon(Icons.Filled.Search, contentDescription = "SearchEntity")
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
                    onClick = {
                        if (selectedSongs.isNotEmpty()) {
                            showListingSheet = true
                        }
                    }
                ) {
                    Icon(Icons.Default.FormatListNumbered, contentDescription = "ListingEntity")
                }
            }
        },
        showGoBack = selectedSongs.isNotEmpty(),
        onNavIconClick = onClearSelection
    )
}
