package com.songlib.presentation.screens.home

import androidx.compose.foundation.layout.*
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.*
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.*
import androidx.navigation.NavHostController
import com.songlib.domain.entity.UiState
import com.songlib.presentation.components.indicators.LoadingState
import com.songlib.presentation.screens.home.tabs.*
import com.songlib.presentation.screens.home.components.*
import com.songlib.presentation.viewmodels.HomeViewModel
import com.songlib.presentation.components.indicators.ErrorState

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun HomeScreen(
    navController: NavHostController,
    viewModel: HomeViewModel,
) {
    var fetchData by rememberSaveable { mutableIntStateOf(0) }
    if (fetchData == 0) {
        viewModel.fetchData()
    }
    val uiState by viewModel.uiState.collectAsState()
    val selectedTab by viewModel.selectedTab.collectAsState()

    Scaffold(
        bottomBar = {
            BottomNavBar(
                selectedItem = selectedTab,
                onItemSelected = viewModel::setSelectedTab
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            when (uiState) {
                is UiState.Error -> ErrorState(
                    message = (uiState as UiState.Error).message,
                    retryAction = { viewModel.fetchData() }
                )

                is UiState.Loading -> LoadingState(title = "", fileName = "circle-loader")
                else -> {
                    when (selectedTab) {
                        HomeNavItem.Search -> HomeSearch(viewModel, navController)
                        HomeNavItem.Likes -> HomeLikes(viewModel, navController)
                        HomeNavItem.Listing -> HomeListings(viewModel, navController)
                    }
                }
            }
        }
    }
}
