package com.songlib.presentation.listing.view

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.songlib.data.models.*
import com.songlib.domain.entity.UiState
import com.songlib.presentation.components.action.AppTopBar
import com.songlib.presentation.components.general.*
import com.songlib.presentation.components.indicators.*
import com.songlib.presentation.listing.ListingViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListingScreen(
    navController: NavHostController, viewModel: ListingViewModel, listing: ListingUi?,
) {
    val uiState by viewModel.uiState.collectAsState()
    val listingTitle by viewModel.listingTitle.collectAsState()
    val listedSongs by viewModel.listedSongs.collectAsState(initial = emptyList())
    var selectedSongs by remember { mutableStateOf<Set<Song>>(emptySet()) }
    var showEditAlert by rememberSaveable { mutableStateOf(false) }
    var showDeleteAlert by rememberSaveable { mutableStateOf(false) }

    if (showEditAlert) {
        QuickFormDialog(
            title = "Edit List Title",
            label = "Listing title",
            initialValue = listingTitle,
            onDismiss = { showEditAlert = false },
            onConfirm = { title ->
                viewModel.saveListing(title)
                showEditAlert = false
            }
        )
    }

    if (showDeleteAlert) {
        ConfirmDialog(
            title = "Delete this listing",
            message = "Are you sure you want to delete the listing: $listingTitle",
            onDismiss = { showDeleteAlert = false },
            onConfirm = {
                viewModel.deleteListing(listing?.id ?: 0)
                navController.popBackStack()
            }
        )
    }

    LaunchedEffect(listing) {
        listing?.let { viewModel.loadListing(it) }
    }

    Scaffold(
        topBar = {
            AppTopBar(
                title = listingTitle,
                actions = {
                    IconButton(onClick = { showEditAlert = true }) {
                        Icon(Icons.Default.Edit, contentDescription = "")
                    }
                    IconButton(onClick = { showDeleteAlert = true }) {
                        Icon(Icons.Default.Delete, contentDescription = "")
                    }
                },
                showGoBack = true,
                onNavIconClick = { navController.popBackStack() },
            )
        }
    ) {
        Box(
            modifier = Modifier
                .padding(it)
                .fillMaxSize()
        ) {
            when (uiState) {
                is UiState.Error -> ErrorState(
                    message = (uiState as UiState.Error).message,
                    retryAction = { }
                )

                UiState.Loaded -> {
                    if (listedSongs.isEmpty()) {
                        EmptyState(
                            message = "Start adding songs to this song list,\\nif you don't want to see this again",
                            messageIcon = Icons.Default.FavoriteBorder
                        )
                    } else {
                        ListedSongs(
                            songs = listedSongs,
                            navController = navController,
                            selectedSongs = selectedSongs,
                            onSongSelected = { }
                        )
                    }
                }

                UiState.Loading -> LoadingState(
                    title = "Loading listing ...",
                    fileName = "circle-loader"
                )

                else -> EmptyState()
            }
        }
    }
}
