package com.songlib.feature.home.view.tabs

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FormatListNumbered
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.songlib.core.common.entity.UiState
import com.songlib.core.common.utils.Routes
import com.songlib.core.database.model.ListingUi
import com.songlib.core.ui.components.action.AppTopBar
import com.songlib.core.ui.components.general.ConfirmDialog
import com.songlib.core.ui.components.general.QuickFormDialog
import com.songlib.core.ui.components.indicators.EmptyState
import com.songlib.feature.home.HomeViewModel
import com.songlib.feature.home.components.ListingsList

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

    LaunchedEffect(showAddAlert) {
        if (showAddAlert && listings.size >= 3) {
            showAddAlert = false
            viewModel.checkAndHandleNewListing()
        }
    }

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
            title = "Delete ${if (selectedListings.size == 1) "this listing" else "these listings"}",
            message = "Are you sure you want to delete the selected listing${if (selectedListings.size != 1) "s" else ""}?",
            onDismiss = { showDeleteAlert = false },
            onConfirm = {
                viewModel.deleteListings(selectedListings)
                showDeleteAlert = false
                selectedListings = emptySet()
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
                            Icon(Icons.Filled.Add, contentDescription = "New listing")
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
                .padding(innerPadding)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            when (uiState) {
                is UiState.Filtered ->
                    if (listings.isEmpty()) {
                        EmptyState(
                            message = "Create your first listing to group songs together",
                            messageIcon = Icons.Default.FormatListNumbered,
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
