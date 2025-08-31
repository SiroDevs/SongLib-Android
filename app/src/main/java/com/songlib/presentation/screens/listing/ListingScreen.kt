package com.songlib.presentation.screens.listing

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.songlib.data.models.*
import com.songlib.domain.entity.UiState
import com.songlib.presentation.components.action.AppTopBar
import com.songlib.presentation.components.indicators.*
import com.songlib.presentation.viewmodels.ListingViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListingScreen(
    navController: NavHostController,
    viewModel: ListingViewModel,
    listing: Listing?,
) {
    val uiState by viewModel.uiState.collectAsState()
    val title by viewModel.title.collectAsState()
    val listings by viewModel.listings.collectAsState(initial = emptyList())

    LaunchedEffect(listing) {
        listing?.let { viewModel.loadListing(it) }
    }

    Scaffold(topBar = {
        Surface(shadowElevation = 3.dp) {
            AppTopBar(
                title = title,
                actions = {

                },
                showGoBack = true,
                onNavIconClick = { navController.popBackStack() },
            )
        }
    }, content = {
        Box(
            modifier = Modifier
                .padding(it)
                .fillMaxSize()
        ) {
            when (uiState) {
                is UiState.Error -> ErrorState(
                    message = (uiState as UiState.Error).message,
                    retryAction = { }
                )

                UiState.Loaded -> EmptyState()

                UiState.Loading -> LoadingState(
                    title = "Loading song ...",
                    fileName = "circle-loader"
                )

                else -> EmptyState()
            }
        }
    })
}
