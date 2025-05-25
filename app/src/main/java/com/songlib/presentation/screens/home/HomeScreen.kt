package com.songlib.presentation.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.pullrefresh.*
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.*
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.songlib.domain.entities.UiState
import com.songlib.presentation.components.action.AppTopBar
import com.songlib.presentation.screens.home.widgets.*
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
    var isSearching by rememberSaveable { mutableStateOf(false) }
    var searchQuery by rememberSaveable { mutableStateOf("") }

    val uiState by viewModel.uiState.collectAsState()
    val selectedTab by viewModel.selectedTab.collectAsState()

    val isRefreshing = uiState is UiState.Loading
    val pullRefreshState = rememberPullRefreshState(
        refreshing = isRefreshing,
        onRefresh = { viewModel.refreshData() }
    )

    Scaffold(
        topBar = {
            if (isSearching) {
                TextField(
                    value = searchQuery,
                    onValueChange = {
                        searchQuery = it
                        viewModel.searchSongs(it)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    placeholder = { Text("Search songs") },
                    singleLine = true,
                    trailingIcon = {
                        IconButton(onClick = {
                            isSearching = false
                            searchQuery = ""
                            viewModel.searchSongs("")
                        }) {
                            Icon(Icons.Filled.Close, contentDescription = "Close")
                        }
                    }
                )
            } else {
                AppTopBar(
                    title = "SongLib",
                    actions = {
                        if (uiState != UiState.Loading) {
                            IconButton(onClick = { isSearching = true }) {
                                Icon(Icons.Filled.Search, contentDescription = "Search")
                            }
                        }
                        IconButton(onClick = { }) {
                            Icon(Icons.Filled.Settings, contentDescription = "Settings")
                        }
                    }
                )
            }
        },
        bottomBar = {
            BottomNavigationBar(
                selectedItem = selectedTab,
                onItemSelected = viewModel::setSelectedTab
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .pullRefresh(pullRefreshState)
        ) {
            HomeContent(
                viewModel = viewModel,
                navController = navController,
                selectedTab = selectedTab,
                isRefreshing = isRefreshing,
                pullRefreshState = pullRefreshState
            )
        }
    }
}
