package com.songlib.feature.home.view

import androidx.compose.foundation.layout.*
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.songlib.core.common.entity.UiState
import com.songlib.core.ui.components.indicators.*
import com.songlib.feature.home.HomeViewModel
import com.songlib.core.ui.components.indicators.ErrorState
import com.songlib.feature.home.components.*
import com.songlib.feature.home.view.tabs.*
import com.songlib.core.common.utils.Routes

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun HomeScreen(
    navController: NavHostController,
    viewModel: HomeViewModel,
) {
    val uiState by viewModel.uiState.collectAsState()
    val selectedTab by viewModel.selectedTab.collectAsState()
    val songs by viewModel.songs.collectAsState(initial = emptyList())

    LaunchedEffect(Unit) {
        viewModel.fetchData()
    }

    when (uiState) {
        is UiState.Error -> {
            ErrorState(
                message = (uiState as UiState.Error).message,
                retryAction = { viewModel.fetchData() }
            )
        }

        UiState.Loading -> {
            LoadingState(title = "", fileName = "circle-loader")
        }

        UiState.Filtered -> {
            if (songs.isEmpty()) {
                EmptyState(
                    message = "It appears you didn't finish your songbook selection, that's why it's empty here at the moment.\n\nLet's fix that asap!",
                    messageIcon = Icons.Default.EditNote,
                    onAction = {
                        viewModel.clearData { success ->
                            if (success) {
                                navController.navigate(Routes.SPLASH) {
                                    popUpTo(0) { inclusive = true }
                                    launchSingleTop = true
                                }
                            }
                        }
                    }
                )
            } else {
                MainHomeContent(
                    selectedTab = selectedTab,
                    onTabSelected = viewModel::setSelectedTab,
                    viewModel = viewModel,
                    navController = navController
                )
            }
        }

        else -> {
            EmptyState()
        }
    }
}

@Composable
private fun MainHomeContent(
    selectedTab: HomeNavItem,
    onTabSelected: (HomeNavItem) -> Unit,
    viewModel: HomeViewModel,
    navController: NavHostController
) {
    Scaffold(
        bottomBar = {
            BottomNavBar(
                selectedItem = selectedTab,
                onItemSelected = onTabSelected
            )
        }
    ) { paddingValues ->
        when (selectedTab) {
            HomeNavItem.Search -> HomeSearch(
                viewModel = viewModel,
                navController = navController,
            )
            HomeNavItem.Likes -> HomeLikes(
                viewModel = viewModel,
                navController = navController,
            )
            HomeNavItem.Listings -> HomeListings(
                viewModel = viewModel,
                navController = navController,
            )
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        )
    }
}

