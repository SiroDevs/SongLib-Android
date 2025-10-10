package com.songlib.presentation.screens.home.tabs

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.window.*
import androidx.navigation.NavHostController
import com.revenuecat.purchases.ui.revenuecatui.*
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
    var showPaywall by remember { mutableStateOf(false) }
    val listings by viewModel.listings.collectAsState(initial = emptyList())
    val isProUser by viewModel.isProUser.collectAsState()
    val showProLimitDialog by viewModel.showProLimitDialog.collectAsState()
    var selectedListings by remember { mutableStateOf<Set<ListingUi>>(emptySet()) }

    LaunchedEffect(showAddAlert) {
        if (showAddAlert) {
            if (!isProUser && listings.size >= 3) {
                showAddAlert = false
                viewModel.checkAndHandleNewListing()
            }
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

    if (showProLimitDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.onProLimitDismiss() },
            title = { Text("Support us by upgrading") },
            text = {
                Text("Please purchase a subscription if you want to continue using this feature and all other Pro features.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.onProLimitProceed()
                        showPaywall = true
                    }
                ) {
                    Text("Upgrade")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { viewModel.onProLimitDismiss() }
                ) {
                    Text("Not Now")
                }
            }
        )
    }

    if (showPaywall) {
        Dialog(
            onDismissRequest = { showPaywall = false },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            val paywallOptions = remember {
                PaywallOptions.Builder(dismissRequest = { showPaywall = false })
                    .setShouldDisplayDismissButton(true)
                    .build()
            }
            Box {
                Paywall(paywallOptions)
            }
        }
    }

    Scaffold(
        topBar = {
            AppTopBar(
                title = if (selectedListings.isEmpty()) "Song Listings" else "${selectedListings.size} selected",
                actions = {
                    if (selectedListings.isEmpty()) {
                        IconButton(onClick = {
                            if (!isProUser && listings.isNotEmpty()) {
                                viewModel.checkAndHandleNewListing()
                            } else {
                                showAddAlert = true
                            }
                        }) {
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
            Column(modifier = Modifier.fillMaxSize()) {
                if (!isProUser && listings.isNotEmpty()) {
                    UpgradeBanner(
                        onUpgradeClick = { showPaywall = true }
                    )
                }

                when (uiState) {
                    is UiState.Filtered ->
                        if (listings.isEmpty()) {
                            EmptyState(
                                message = "Start adding lists of songs,\nif you don't want to see this again",
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
}
