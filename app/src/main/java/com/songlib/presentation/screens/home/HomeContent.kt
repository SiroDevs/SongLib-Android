package com.songlib.presentation.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.pullrefresh.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.navigation.NavHostController
import com.songlib.domain.entity.UiState
import com.songlib.presentation.components.ErrorState
import com.songlib.presentation.components.LoadingState
import com.songlib.presentation.screens.home.likes.LikesScreen
import com.songlib.presentation.screens.home.search.SearchScreen
import com.songlib.presentation.screens.home.widgets.*
import com.songlib.presentation.theme.ThemeColors
import com.songlib.presentation.viewmodels.HomeViewModel

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun HomeContent(
    viewModel: HomeViewModel,
    navController: NavHostController,
    selectedTab: HomeNavItem,
    isRefreshing: Boolean,
    pullRefreshState: PullRefreshState
) {
    val uiState by viewModel.uiState.collectAsState()

    when (uiState) {
        is UiState.Error -> ErrorState(
            errorMessage = (uiState as UiState.Error).errorMessage,
            onRetry = { viewModel.fetchData() }
        )

        UiState.Loading -> LoadingState("Loading data ...")

        else -> {
            Box(
                modifier = Modifier
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
                    contentColor = ThemeColors.primary1
                )
            }
        }
    }
}
