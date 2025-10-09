package com.songlib.presentation.screens.home

import androidx.compose.foundation.layout.*
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.window.*
import androidx.navigation.NavHostController
import com.revenuecat.purchases.ui.revenuecatui.*
import com.revenuecat.purchases.ui.revenuecatui.customercenter.CustomerCenter
import com.songlib.domain.entity.UiState
import com.songlib.presentation.components.indicators.*
import com.songlib.presentation.screens.home.tabs.*
import com.songlib.presentation.screens.home.components.*
import com.songlib.presentation.viewmodels.HomeViewModel
import com.songlib.presentation.components.indicators.ErrorState
import com.songlib.presentation.navigation.Routes

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun HomeScreen(
    navController: NavHostController,
    viewModel: HomeViewModel,
) {
    val uiState by viewModel.uiState.collectAsState()
    val selectedTab by viewModel.selectedTab.collectAsState()
    val songs by viewModel.songs.collectAsState(initial = emptyList())

    val canShowPaywall by viewModel.canShowPaywall.collectAsState()
    var showPaywall by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.fetchData()
        showPaywall = canShowPaywall
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
                if (canShowPaywall) {
                    Paywall(paywallOptions)
                } else {
                    CustomerCenter(onDismiss = { showPaywall = false })
                }
            }
        }
    }

    when (uiState) {
        is UiState.Error -> Scaffold { padding ->
            ScaffoldContent(padding) {
                ErrorState(
                    message = (uiState as UiState.Error).message,
                    retryAction = { viewModel.fetchData() }
                )
            }
        }

        UiState.Loading -> Scaffold { padding ->
            ScaffoldContent(padding) {
                LoadingState(title = "", fileName = "circle-loader")
            }
        }

        UiState.Filtered -> {
            if (songs.isEmpty()) {
                Scaffold { padding ->
                    ScaffoldContent(padding) {
                        EmptyState(
                            message = "It appears you didn't finish your songbook selection, that's why it's empty here at the moment.\n\nLet's fix that asap!",
                            messageIcon = Icons.Default.EditNote,
                            onAction = {
                                viewModel.clearData()
                                navController.navigate(Routes.SPLASH) {
                                    popUpTo(0) { inclusive = true }
                                    launchSingleTop = true
                                }
                            }
                        )
                    }
                }
            } else {
                Scaffold(
                    bottomBar = {
                        BottomNavBar(
                            selectedItem = selectedTab,
                            onItemSelected = viewModel::setSelectedTab
                        )
                    }
                ) { padding ->
                    ScaffoldContent(padding) {
                        when (selectedTab) {
                            HomeNavItem.Search -> HomeSearch(viewModel, navController)
                            HomeNavItem.Likes -> HomeLikes(viewModel, navController)
                            HomeNavItem.Listings -> HomeListings(viewModel, navController)
                        }
                    }
                }
            }
        }

        else -> Scaffold { padding ->
            ScaffoldContent(padding) { EmptyState() }
        }
    }
}

@Composable
private fun ScaffoldContent(
    padding: PaddingValues,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = Modifier
            .padding(padding)
            .fillMaxSize(),
        content = content
    )
}
