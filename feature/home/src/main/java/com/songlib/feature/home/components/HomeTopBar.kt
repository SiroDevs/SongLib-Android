package com.songlib.feature.home.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Cast
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.FormatListNumbered
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavHostController
import com.songlib.core.common.utils.Routes
import com.songlib.core.data.repos.PrefsRepo
import com.songlib.core.database.model.ListingUi
import com.songlib.core.database.model.SongEntity
import com.songlib.core.ui.components.action.AppTopBar
import com.songlib.feature.home.HomeViewModel

@Composable
fun HomeAppBar(
    title: String,
    selectedTab: HomeTab,
    selectedSongs: Set<SongEntity>,
    selectedListings: Set<ListingUi>,
    onClearSongSelection: () -> Unit,
    onClearListingSelection: () -> Unit,
    onLikeSongs: () -> Unit,
    onShareSong: () -> Unit,
    onShowListingSheet: () -> Unit,
    onDeleteListings: () -> Unit,
    onAddListing: () -> Unit,
    viewModel: HomeViewModel,
    navController: NavHostController,
    prefsRepo: PrefsRepo,
) {
    var showMoreMenu by remember { mutableStateOf(false) }
    val hasSelection = selectedSongs.isNotEmpty() || selectedListings.isNotEmpty()
    val hasHistory by viewModel.hasHistory.collectAsState()
    val hasEdits by viewModel.hasEdits.collectAsState()

    AppTopBar(
        title = title,
        showGoBack = hasSelection,
        onNavIconClick = {
            if (selectedSongs.isNotEmpty()) onClearSongSelection()
            else onClearListingSelection()
        },
        actions = {
            when {
                !hasSelection -> {
                    if (selectedTab == HomeTab.Listings) {
                        IconButton(onClick = onAddListing) {
                            Icon(Icons.Default.Add, contentDescription = "New listing")
                        }
                    }

                    IconButton(onClick = { navController.navigate(Routes.BROADCAST) }) {
                        Icon(Icons.Default.Cast, contentDescription = "Broadcast to PC")
                    }

                    if (hasHistory) {
                        IconButton(onClick = { navController.navigate(Routes.HISTORY) }) {
                            Icon(Icons.Default.History, contentDescription = "History")
                        }
                    }

                    if (selectedTab == HomeTab.Search) {
                        IconButton(onClick = { navController.navigate(Routes.DRAFTS) }) {
                            Icon(Icons.Default.EditNote, contentDescription = "Drafts")
                        }
                    }

                    IconButton(onClick = { showMoreMenu = true }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "More")
                    }

                    HomeOverflowMenu(
                        expanded = showMoreMenu,
                        onDismiss = { showMoreMenu = false },
                        hasEdits = hasEdits,
                        isAdmin = prefsRepo.isAdmin,
                        navController = navController,
                    )
                }

                selectedSongs.isNotEmpty() -> {
                    val allLiked = selectedSongs.all { it.liked }
                    IconButton(onClick = onLikeSongs) {
                        Icon(
                            imageVector = if (allLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = if (allLiked) "Unlike" else "Like",
                            tint = if (allLiked) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                    if (selectedSongs.size == 1) {
                        IconButton(onClick = onShareSong) {
                            Icon(Icons.Default.Share, contentDescription = "Share")
                        }
                    }
                    IconButton(onClick = onShowListingSheet) {
                        Icon(
                            Icons.Default.FormatListNumbered,
                            contentDescription = "Add to listing"
                        )
                    }
                }

                selectedListings.isNotEmpty() -> {
                    IconButton(onClick = onDeleteListings) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete")
                    }
                }
            }
        }
    )
}
