package com.songlib.presentation.selection.view

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.*
import androidx.navigation.NavHostController
import com.revenuecat.purchases.ui.revenuecatui.*
import com.songlib.domain.entity.UiState
import com.songlib.domain.repos.*
import com.songlib.presentation.components.action.AppTopBar
import com.songlib.presentation.components.indicators.*
import com.songlib.presentation.navigation.Routes
import com.songlib.presentation.selection.SelectionViewModel
import com.songlib.presentation.selection.components.Step1Fab

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Step1Screen(
    navController: NavHostController,
    viewModel: SelectionViewModel,
    themeRepo: ThemeRepository
) {
    var fetchData by rememberSaveable { mutableIntStateOf(0) }
    var showThemeDialog by remember { mutableStateOf(false) }
    var showPaywall by remember { mutableStateOf(false) }

    if (fetchData == 0) {
        viewModel.fetchBooks()
        fetchData++
    }

    val books by viewModel.books.collectAsState(initial = emptyList())
    val uiState by viewModel.uiState.collectAsState()
    val showUpgradeDialog by viewModel.showUpgradeDialog.collectAsState()
    val isProUser by viewModel.isProUser.collectAsState()
    val theme = themeRepo.selectedTheme

    LaunchedEffect(uiState) {
        if (uiState == UiState.Saved) {
            navController.navigate(Routes.HOME)
        }
    }

    if (showUpgradeDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.onUpgradeDismis() },
            title = { Text("You selected more than 4 Songbooks...") },
            text = {
                Text("Please purchase a subscription if you want to have more than 3 songbooks in your collection.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.onUpgradeProceed()
                        showPaywall = true
                    }
                ) {
                    Text("OKAY")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { viewModel.onUpgradeDismis() }
                ) {
                    Text("CANCEL")
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
            Box() {
                Paywall(paywallOptions)
            }
        }
    }

    if (showThemeDialog) {
        ThemeSelectorDialog(
            current = theme,
            onDismiss = { showThemeDialog = false },
            onThemeSelected = {
                themeRepo.setTheme(it)
                showThemeDialog = false
            }
        )
    }

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Select Songbooks",
                actions = {
                    if (uiState != UiState.Loading && uiState != UiState.Saving) {
                        IconButton(
                            onClick = { viewModel.fetchBooks() }
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Refresh, contentDescription = "",
                            )
                        }
                    }

                    IconButton(onClick = { showThemeDialog = true }) {
                        Icon(
                            imageVector = Icons.Filled.Brightness6, contentDescription = ""
                        )
                    }
                }
            )
        },
        content = { paddingValues ->
            when (uiState) {
                is UiState.Error -> ErrorState(
                    message = (uiState as UiState.Error).message,
                    retryAction = { viewModel.fetchBooks() }
                )

                is UiState.Loading -> LoadingState(
                    title = "Loading books ...",
                    fileName = "loading-hand"
                )

                is UiState.Saving ->
                    LoadingState(
                        title = "Saving books ...",
                        fileName = "cloud-download"
                    )

                is UiState.Loaded -> {
                    SelectionContent(
                        books = books,
                        onBookClick = { viewModel.toggleBookSelection(it) },
                        modifier = Modifier.padding(paddingValues)
                    )
                }

                else -> EmptyState()
            }
        },
        floatingActionButton = {
            if (uiState == UiState.Loaded) {
                Step1Fab(
                    viewModel = viewModel,
                    onSaveConfirmed = { viewModel.saveSelectedBooks() }
                )
            }
        }
    )
}