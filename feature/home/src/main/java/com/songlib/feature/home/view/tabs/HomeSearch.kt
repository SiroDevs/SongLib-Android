package com.songlib.feature.home.view.tabs

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dialpad
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.songlib.core.common.entity.UiState
import com.songlib.core.database.model.SongEntity
import com.songlib.core.ui.components.general.QuickFormDialog
import com.songlib.core.ui.components.indicators.EmptyState
import com.songlib.feature.home.HomeViewModel
import com.songlib.feature.home.components.ChoosingListingSheet
import com.songlib.feature.home.components.DialPad
import com.songlib.feature.home.components.SongsList

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeSearch(
    viewModel: HomeViewModel,
    navController: NavHostController,
) {
    val uiState by viewModel.uiState.collectAsState()
    val searchQry by viewModel.searchQuery.collectAsState()
    val songs by viewModel.filtered.collectAsState(initial = emptyList())
    val listings by viewModel.listings.collectAsState(initial = emptyList())
    val selectedSongs by viewModel.selectedSongs.collectAsState()

    var dialPadVisible by rememberSaveable { mutableStateOf(false) }
    var showAddDialog by remember { mutableStateOf(false) }
    var showListingSheet by remember { mutableStateOf(false) }

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
            onDismiss = { showListingSheet = false },
            onNewListClick = {
                showListingSheet = false
                if (viewModel.checkAndHandleNewListing()) showAddDialog = true
            },
            onListingClick = { listing ->
                viewModel.saveListItems(listing, selectedSongs)
                showListingSheet = false
                viewModel.clearSongSelection()
            },
            onDone = { showListingSheet = false }
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        when (uiState) {
            is UiState.Filtered, UiState.Saving ->
                SongsList(
                    songs = songs,
                    viewModel = viewModel,
                    navController = navController,
                    selectedSongs = selectedSongs,
                    searchQuery = searchQry,
                    onQueryChange = { query -> viewModel.searchSongs(query, byNo = false) },
                    onSongSelected = { song -> viewModel.toggleSongSelection(song) },
                )

            else -> Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                EmptyState()
            }
        }

        FloatingActionButton(
            onClick = { dialPadVisible = true },
            containerColor = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .navigationBarsPadding()
        ) {
            Icon(Icons.Filled.Dialpad, contentDescription = "Search by number")
        }

        if (dialPadVisible) {
            DialPad(
                currentQuery = searchQry,
                onNumberClick = { num -> viewModel.searchSongs(searchQry + num, byNo = true) },
                onBackspaceClick = {
                    if (searchQry.isNotEmpty()) {
                        viewModel.searchSongs(searchQry.dropLast(1), byNo = true)
                    }
                },
                onDismiss = { dialPadVisible = false }
            )
        }
    }
}
