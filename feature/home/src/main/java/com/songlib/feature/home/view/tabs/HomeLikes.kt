package com.songlib.feature.home.view.tabs

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.songlib.core.common.entity.UiState
import com.songlib.core.data.repos.PrefsRepo
import com.songlib.core.ui.components.indicators.EmptyState
import com.songlib.feature.home.HomeViewModel
import com.songlib.feature.home.components.SongsList

@Composable
fun HomeLikes(
    viewModel: HomeViewModel,
    navController: NavHostController,
    prefsRepo: PrefsRepo,
    onShowDonationDialog: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()
    val searchQry by viewModel.searchQuery.collectAsState()
    val likes by viewModel.likes.collectAsState(initial = emptyList())
    val selectedSongs by viewModel.selectedSongs.collectAsState()
    val showDonation = remember { prefsRepo.shouldShowDonation() }

    LaunchedEffect(likes) {
        val likedIds = likes.map { it.songId }.toSet()
        val stale = selectedSongs.filter { it.songId !in likedIds }
        stale.forEach { viewModel.toggleSongSelection(it) }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        when (uiState) {
            is UiState.Filtered, UiState.Saving ->
                if (likes.isEmpty()) {
                    EmptyState(
                        message = "Start liking songs when you view them,\n If you don't want to see this again",
                        messageIcon = Icons.Default.FavoriteBorder
                    )
                } else {
                    SongsList(
                        songs = likes,
                        viewModel = viewModel,
                        navController = navController,
                        selectedSongs = selectedSongs,
                        showSearch = false,
                        showBookFilter = false,
                        onQueryChange = {},
                        searchQuery = searchQry,
                        onSongSelected = { song -> viewModel.toggleSongSelection(song) },
                        showDonation = showDonation,
                        onShowDonationDialog = onShowDonationDialog
                    )
                }
            else -> EmptyState()
        }
    }
}
