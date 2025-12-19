package com.songlib.presentation.home.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import com.songlib.data.models.Song
import com.songlib.domain.repository.PrefsRepo
import com.songlib.presentation.components.action.AppTopBar
import com.songlib.presentation.components.general.QuickFormDialog
import com.songlib.presentation.home.HomeViewModel

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
    val context = LocalContext.current
    val prefs = remember { PrefsRepo(context) }
    var showAddDialog by remember { mutableStateOf(false) }
    var showListingSheet by remember { mutableStateOf(false) }
    val isProUser by viewModel.isProUser.collectAsState()
    val listings by viewModel.listings.collectAsState(initial = emptyList())

    if (showAddDialog) {
        QuickFormDialog(
            title = "New Listing",
            label = "Listing title",
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
            isProUser = isProUser,
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
//        title = if (selectedSongs.isEmpty()) "SongLib" else "${selectedSongs.size} selected",
        title = if (prefs.isDataLoaded) "True" else "False",
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
                    onClick = {
                        // Only show listing sheet if we have songs selected
                        if (selectedSongs.isNotEmpty()) {
                            showListingSheet = true
                        }
                    }
                ) {
                    Icon(Icons.Default.FormatListNumbered, contentDescription = "Listing")
                }
            }
        },
        showGoBack = selectedSongs.isNotEmpty(),
        onNavIconClick = onClearSelection
    )
}
