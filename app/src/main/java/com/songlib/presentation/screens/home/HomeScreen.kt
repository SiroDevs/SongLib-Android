package com.songlib.presentation.screens.home

import androidx.compose.foundation.layout.*
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.*
import androidx.navigation.NavHostController
import com.songlib.domain.entity.UiState
import com.songlib.presentation.components.*
import com.songlib.presentation.components.action.*
import com.songlib.presentation.navigation.Routes
import com.songlib.presentation.screens.home.likes.LikesScreen
import com.songlib.presentation.screens.home.search.SearchScreen
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

    Scaffold(
        topBar = {
            if (isSearching) {
                SearchTopBar(
                    query = searchQuery,
                    onQueryChange = {
                        searchQuery = it
                        viewModel.searchSongs(it)
                    },
                    onClose = {
                        isSearching = false
                        searchQuery = ""
                        viewModel.searchSongs("")
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

                        IconButton(onClick = { navController.navigate(Routes.SETTINGS) }) {
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
                .fillMaxSize()
        ) {

            when (uiState) {
                is UiState.Error -> ErrorState(
                    errorMessage = (uiState as UiState.Error).errorMessage,
                    onRetry = { viewModel.fetchData() }
                )
                is UiState.Loading -> LoadingState("Loading data ...")
                else -> {
                    when (selectedTab) {
                        HomeNavItem.Search -> SearchScreen(viewModel, navController)
                        HomeNavItem.Likes -> LikesScreen(viewModel)
                    }
                }
            }
        }
    }
}
