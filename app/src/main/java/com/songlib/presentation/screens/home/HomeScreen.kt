package com.songlib.presentation.screens.home

import androidx.compose.foundation.layout.*
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.*
import androidx.navigation.NavHostController
import com.songlib.domain.entity.UiState
import com.songlib.presentation.components.action.*
import com.songlib.presentation.components.indicators.LoadingState
import com.songlib.presentation.navigation.Routes
import com.songlib.presentation.screens.home.tabs.*
import com.songlib.presentation.screens.home.components.*
import com.songlib.presentation.viewmodels.HomeViewModel
import com.swahilib.presentation.components.indicators.ErrorState

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
    var isSearching by rememberSaveable { mutableStateOf(false) }
    var searchByNo by rememberSaveable { mutableStateOf(false) }
    var searchQry by rememberSaveable { mutableStateOf("") }

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
                    onRetry = { viewModel.fetchData() }
                )

                is UiState.Loading -> LoadingState(title = "", fileName = "circle-loader")
                else -> {
                    when (selectedTab) {
                        HomeNavItem.Search -> HomeSearch(viewModel, navController)
                        HomeNavItem.Likes -> HomeLikes(viewModel, navController)
                        HomeNavItem.Listing -> HomeLikes(viewModel, navController)
                    }
                }
            }
        }
    }

    if (searchByNo) {
        DialPadOverlay(
            visible = true,
            onNumberClick = { num ->
                searchQry += num
                viewModel.searchSongs(searchQry, true)
            },
            onBackspaceClick = {
                if (searchQry.isNotEmpty()) {
                    searchQry = searchQry.dropLast(1)
                    viewModel.searchSongs(searchQry, true)
                }
            },
            onSearchClick = {
                viewModel.searchSongs(searchQry, true)
                isSearching = false
                searchByNo = false
            }
        )
    }
}
