package com.songlib.presentation.home.view

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
import com.songlib.domain.entity.UiState
import com.songlib.presentation.components.indicators.*
import com.songlib.presentation.home.HomeViewModel
import com.songlib.presentation.components.indicators.ErrorState
import com.songlib.presentation.home.components.*
import com.songlib.presentation.home.view.tabs.*
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

