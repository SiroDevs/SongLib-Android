package com.songlib.feature.home.view.tabs

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Dialpad
import androidx.compose.material.icons.filled.FormatListNumbered
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.songlib.core.common.entity.UiState
import com.songlib.core.database.model.ListingUi
import com.songlib.core.ui.components.general.ConfirmDialog
import com.songlib.core.ui.components.general.QuickFormDialog
import com.songlib.core.ui.components.indicators.EmptyState
import com.songlib.feature.home.HomeViewModel
import com.songlib.feature.home.components.ListingsList

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeListings(
    viewModel: HomeViewModel,
    navController: NavHostController,
) {
    val uiState by viewModel.uiState.collectAsState()
    var showAddAlert by remember { mutableStateOf(false) }
    var showDeleteAlert by remember { mutableStateOf(false) }
    val listings by viewModel.listings.collectAsState(initial = emptyList())
    val selectedListings by viewModel.selectedListings.collectAsState()

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
            }
        )
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        FloatingActionButton(
            onClick = { showAddAlert = true },
            containerColor = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .navigationBarsPadding()
                .padding(end = 15.dp)
        ) {
            Icon(Icons.Filled.Add, contentDescription = "New listing")
        }

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
                        onListingSelected = { listing -> viewModel.toggleListingSelection(listing) },
                    )
                }
            else -> EmptyState()
        }
    }
}
