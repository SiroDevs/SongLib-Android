package com.songlib.presentation.screens.home

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.pullrefresh.*
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import com.songlib.domain.entities.UiState
import com.songlib.presentation.components.*
import com.songlib.presentation.components.action.AppTopBar
import com.songlib.presentation.screens.home.likes.LikesScreen
import com.songlib.presentation.screens.home.search.SearchScreen
import com.songlib.presentation.screens.home.widgets.*
import com.songlib.presentation.theme.ThemeColors
import com.songlib.presentation.viewmodels.HomeViewModel

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    navController: NavHostController,
) {
    var fetchData by rememberSaveable { mutableStateOf(0) }

    if (fetchData == 0) {
        viewModel.fetchData()
        fetchData++
    }

    val uiState by viewModel.uiState.collectAsState()

    val isRefreshing = uiState is UiState.Loading
    val pullRefreshState = rememberPullRefreshState(
        refreshing = isRefreshing,
        onRefresh = { viewModel.fetchData() }
    )

    var selectedTab by rememberSaveable { mutableStateOf<HomeNavItem>(HomeNavItem.Search) }

    when (uiState) {
        is UiState.Error -> Scaffold(
            topBar = { AppTopBar(title = "SongLib") },
            content = { padding ->
                Box(modifier = Modifier.padding(padding)) {
                    ErrorState(
                        errorMessage = (uiState as UiState.Error).errorMessage,
                        onRetry = { viewModel.fetchData() }
                    )
                }
            }
        )

        UiState.Loading -> Scaffold(
            topBar = { AppTopBar(title = "SongLib") },
            content = { padding ->
                Box(modifier = Modifier.padding(padding)) {
                    LoadingState("Loading data ...")
                }
            }
        )

        else -> HomeContent(
            viewModel = viewModel,
            navController = navController,
            selectedTab = selectedTab,
            onTabSelected = { selectedTab = it },
            isRefreshing = isRefreshing,
            pullRefreshState = pullRefreshState
        )
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun HomeContent(
    viewModel: HomeViewModel,
    navController: NavHostController,
    selectedTab: HomeNavItem,
    onTabSelected: (HomeNavItem) -> Unit,
    isRefreshing: Boolean,
    pullRefreshState: PullRefreshState
) {
    Scaffold(
        topBar = {
            AppTopBar(
                title = "SongLib",
                actions = {
                    IconButton(onClick = { }) {
                        Icon(Icons.Filled.Search, contentDescription = "Search")
                    }
                    IconButton(onClick = { }) {
                        Icon(Icons.Filled.Settings, contentDescription = "Settings")
                    }
                }
            )
        },
        bottomBar = {
            BottomNavigationBar(
                selectedItem = selectedTab,
                onItemSelected = onTabSelected
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .pullRefresh(pullRefreshState)
        ) {
            when (selectedTab) {
                HomeNavItem.Search -> SearchScreen(viewModel, navController)
                HomeNavItem.Likes -> LikesScreen(viewModel)
            }

            PullRefreshIndicator(
                refreshing = isRefreshing,
                state = pullRefreshState,
                modifier = Modifier.align(Alignment.TopCenter),
                contentColor = ThemeColors.primary
            )
        }
    }
}
