package com.songlib.feature.home.view

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.songlib.core.common.entity.UiState
import com.songlib.feature.home.HomeViewModel
import com.songlib.core.ui.components.indicators.ErrorState
import com.songlib.core.common.utils.Routes
import com.songlib.core.ui.components.indicators.EmptyState
import com.songlib.core.ui.components.indicators.LoadingState
import com.songlib.feature.home.components.BottomNavBar
import com.songlib.feature.home.components.HomeNavItem
import com.songlib.feature.home.view.tabs.HomeLikes
import com.songlib.feature.home.view.tabs.HomeListings
import com.songlib.feature.home.view.tabs.HomeSearch

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

        else ->  EmptyState()
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

