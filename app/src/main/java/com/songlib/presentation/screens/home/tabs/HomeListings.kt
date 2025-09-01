package com.songlib.presentation.screens.home.tabs

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.navigation.NavHostController
import com.songlib.presentation.screens.home.components.ListingsList
import com.songlib.data.models.ListingUi
import com.songlib.domain.entity.UiState
import com.songlib.presentation.components.action.*
import com.songlib.presentation.components.general.*
import com.songlib.presentation.components.indicators.*
import com.songlib.presentation.navigation.Routes
import com.songlib.presentation.viewmodels.HomeViewModel
import kotlin.collections.plus

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeListings(
    viewModel: HomeViewModel,
    navController: NavHostController
) {
    val uiState by viewModel.uiState.collectAsState()
    var showAddAlert by remember { mutableStateOf(false) }
    var showDeleteAlert by remember { mutableStateOf(false) }
    val listings by viewModel.listings.collectAsState(initial = emptyList())
    var selectedListings by remember { mutableStateOf<Set<ListingUi>>(emptySet()) }

    if (showAddAlert) {
        QuickFormDialog(
            title = "New Listing",
            label = "Listing title",
            onDismiss = { showAddAlert = false },
            onConfirm = { title ->
                viewModel.saveListing(title)
                showAddAlert = false
            }
        )
    }

    if (showDeleteAlert) {
        ConfirmDialog(
            title = "Delete this listing${selectedListings.size} == 1 ? 's' : ''",
            message = "Are you sure you want to deleted the selected listings?",
            onDismiss = { showDeleteAlert = false },
            onConfirm = {
                viewModel.deleteListings(selectedListings)
                showDeleteAlert = false
            }
        )
    }

    Scaffold(
        topBar = {
            AppTopBar(
                title = if (selectedListings.isEmpty()) "Song Listings" else "${selectedListings.size} selected",
                actions = {
                    if (selectedListings.isEmpty()) {
                        IconButton(onClick = { showAddAlert = true }) {
                            Icon(Icons.Filled.Add, contentDescription = "New")
                        }
                        IconButton(onClick = { navController.navigate(Routes.SETTINGS) }) {
                            Icon(Icons.Filled.Settings, contentDescription = "Settings")
                        }
                    } else {
                        IconButton(onClick = { showDeleteAlert = true }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete")
                        }
                    }
                },
                showGoBack = selectedListings.isNotEmpty(),
                onNavIconClick = { selectedListings = emptySet() }
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
                        ListingsList(
                            listings = listings,
                            navController = navController,
                            selectedListings = selectedListings,
                            onListingSelected = { listing ->
                                selectedListings =
                                    if (selectedListings.contains(listing)) selectedListings - listing
                                    else selectedListings + listing
                            },
                        )
                    }
                else -> EmptyState()
            }
        }
    }
}
