package com.songlib.feature.home.view.tabs

import android.content.Intent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Brightness6
import androidx.compose.material.icons.filled.Dialpad
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.FormatListNumbered
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.songlib.core.common.entity.UiState
import com.songlib.core.common.utils.Routes
import com.songlib.core.common.utils.lyricsString
import com.songlib.core.common.utils.songShareString
import com.songlib.core.database.model.SongEntity
import com.songlib.core.ui.components.action.AppTopBar
import com.songlib.core.ui.components.general.QuickFormDialog
import com.songlib.core.ui.components.indicators.EmptyState
import com.songlib.feature.home.HomeViewModel
import com.songlib.feature.home.components.ChoosingListingSheet
import com.songlib.feature.home.components.DialPad
import com.songlib.feature.home.components.SongsList

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeSearch(
    viewModel: HomeViewModel,
    navController: NavHostController,
    onShowThemeDialog: () -> Unit,
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    val searchQry by viewModel.searchQuery.collectAsState()
    val songs by viewModel.filtered.collectAsState(initial = emptyList())
    val listings by viewModel.listings.collectAsState(initial = emptyList())

    var selectedSongs by remember { mutableStateOf<Set<SongEntity>>(emptySet()) }
    var dialPadVisible by rememberSaveable { mutableStateOf(false) }
    var showAddDialog by remember { mutableStateOf(false) }
    var showListingSheet by remember { mutableStateOf(false) }

    if (showAddDialog) {
        QuickFormDialog(
            title = "New Listing",
            label = "Listing title",
            onDismiss = { showAddDialog = false },
            onConfirm = { title ->
                if (viewModel.checkAndHandleNewListing()) {
                    viewModel.saveListing(title)
                    showAddDialog = false
                }
            }
        )
    }

    if (showListingSheet) {
        ChoosingListingSheet(
            listings = listings,
            onDismiss = { showListingSheet = false },
            onNewListClick = {
                showListingSheet = false
                if (viewModel.checkAndHandleNewListing()) showAddDialog = true
            },
            onListingClick = { listing ->
                viewModel.saveListItems(listing, selectedSongs)
                showListingSheet = false
                selectedSongs = emptySet()
            },
            onDone = { showListingSheet = false }
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                if (selectedSongs.isEmpty()) {
                    AppTopBar(
                        title = "SongLib",
                        actions = {
                            IconButton(onClick = onShowThemeDialog ) {
                                Icon(
                                    imageVector = Icons.Filled.Brightness6, contentDescription = ""
                                )
                            }
                            IconButton(onClick = {
                                navController.navigate(Routes.SETTINGS)
                            }) {
                                Icon(Icons.Filled.Settings, contentDescription = "Settings")
                            }
                        }
                    )
                } else {
                    val allLiked = selectedSongs.all { it.liked }
                    AppTopBar(
                        title = "${selectedSongs.size} selected",
                        showGoBack = true,
                        onNavIconClick = { selectedSongs = emptySet() },
                        actions = {
                            IconButton(onClick = {
                                viewModel.likeSongs(selectedSongs)
                                selectedSongs = emptySet()
                            }) {
                                Icon(
                                    if (allLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                    contentDescription = if (allLiked) "Unlike" else "Like",
                                    tint = if (allLiked) MaterialTheme.colorScheme.primary else LocalContentColor.current
                                )
                            }
                            if (selectedSongs.size == 1) {
                                IconButton(onClick = {
                                    val song = selectedSongs.first()
                                    val shareText = songShareString(song.title, lyricsString(song.content))
                                    val intent = Intent(Intent.ACTION_SEND).apply {
                                        type = "text/plain"
                                        putExtra(Intent.EXTRA_TEXT, shareText)
                                    }
                                    context.startActivity(Intent.createChooser(intent, "Share song via"))
                                    selectedSongs = emptySet()
                                }) {
                                    Icon(Icons.Default.Share, contentDescription = "Share")
                                }
                            }
                            IconButton(onClick = { showListingSheet = true }) {
                                Icon(Icons.Default.FormatListNumbered, contentDescription = "Add to listing")
                            }
                        }
                    )
                }
            },
            floatingActionButton = {
                if (selectedSongs.isEmpty()) {
                    FloatingActionButton(
                        onClick = { dialPadVisible = true },
                        containerColor = MaterialTheme.colorScheme.onPrimary
                    ) {
                        Icon(Icons.Filled.Dialpad, contentDescription = "Search by number")
                    }
                }
            },
        ) { innerPadding ->
            when (uiState) {
                is UiState.Filtered, UiState.Saving ->
                    SongsList(
                        songs = songs,
                        viewModel = viewModel,
                        navController = navController,
                        selectedSongs = selectedSongs,
                        searchQuery = searchQry,
                        onQueryChange = { query -> viewModel.searchSongs(query, byNo = false) },
                        onSongSelected = { song ->
                            selectedSongs =
                                if (selectedSongs.contains(song)) selectedSongs - song
                                else selectedSongs + song
                        },
                        contentPadding = innerPadding,
                    )

                else -> Box(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    EmptyState()
                }
            }
        }

        if (dialPadVisible) {
            DialPad(
                currentQuery = searchQry,
                onNumberClick = { num -> viewModel.searchSongs(searchQry + num, byNo = true) },
                onBackspaceClick = {
                    if (searchQry.isNotEmpty()) {
                        viewModel.searchSongs(searchQry.dropLast(1), byNo = true)
                    }
                },
                onDismiss = {
                    dialPadVisible = false
                }
            )
        }
    }
}