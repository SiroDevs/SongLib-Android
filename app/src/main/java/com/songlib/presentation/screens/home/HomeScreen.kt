package com.songlib.presentation.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import com.songlib.domain.entities.UiState
import com.songlib.presentation.components.*
import com.songlib.presentation.components.action.AppTopBar
import com.songlib.presentation.screens.home.likes.LikesScreen
import com.songlib.presentation.screens.home.search.SearchScreen
import com.songlib.presentation.screens.home.widgets.*
import com.songlib.presentation.viewmodels.HomeViewModel

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
) {
    val navController = rememberNavController()
    val uiState by viewModel.uiState.collectAsState()

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

        UiState.Loading ->
            Scaffold(
                topBar = { AppTopBar(title = "SongLib") },
                content = { padding ->
                    Box(modifier = Modifier.padding(padding)) {
                        LoadingState("Loading books ...")
                    }
                }
            )

        else -> HomeContent(viewModel, navController)
    }
}

@Composable
fun HomeContent(viewModel: HomeViewModel, navController: NavHostController) {
    Scaffold(
        topBar = {
            AppTopBar(
                title = "SongLib",
                actions = {
                    IconButton(
                        onClick = { }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Search,
                            contentDescription = "Search"
                        )
                    }
                    IconButton(
                        onClick = { }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Settings,
                            contentDescription = "Settings"
                        )
                    }
                }
            )
        },
        content = { padding ->
            Box(modifier = Modifier.padding(padding)) {
                Navigation(viewModel, navController)
            }
        },
        bottomBar = { BottomNavigationBar(navController) },
    )
}

@Composable
fun Navigation(viewModel: HomeViewModel, navController: NavHostController) {
    NavHost(navController, startDestination = NavigationItem.Search.route) {
        composable(NavigationItem.Search.route) {
            SearchScreen(viewModel)
        }
        composable(NavigationItem.Likes.route) {
            LikesScreen()
        }
    }
}