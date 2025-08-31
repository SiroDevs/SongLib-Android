package com.songlib.presentation.screens.home.tabs

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.*
import androidx.navigation.NavHostController
import com.songlib.domain.entity.UiState
import com.songlib.presentation.components.action.*
import com.songlib.presentation.navigation.Routes
import com.songlib.presentation.viewmodels.HomeViewModel
import androidx.compose.ui.Modifier
import com.songlib.data.models.Song
import com.songlib.presentation.components.indicators.EmptyState
import com.songlib.presentation.screens.home.components.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeSearch( viewModel: HomeViewModel, navController: NavHostController) {
    val uiState by viewModel.uiState.collectAsState()
    var isSearching by rememberSaveable { mutableStateOf(false) }
    var searchByNo by rememberSaveable { mutableStateOf(false) }
    var searchQry by rememberSaveable { mutableStateOf("") }
    val songs by viewModel.filtered.collectAsState(initial = emptyList())
    var selectedSong by remember { mutableStateOf<Song?>(null) }

    Scaffold(
        topBar = {
            if (isSearching) {
                SearchTopBar(
                    query = searchQry,
                    onQueryChange = {
                        searchQry = it
                        viewModel.searchSongs(it, searchByNo)
                    },
                    onClose = {
                        isSearching = false
                        searchByNo = false
                        searchQry = ""
                        viewModel.searchSongs("")
                    }
                )
            } else {
                AppTopBar(
                    actions = {
                        IconButton(onClick = { isSearching = true }) {
                            Icon(Icons.Filled.Search, contentDescription = "")
                        }
                        IconButton(onClick = { navController.navigate(Routes.SETTINGS) }) {
                            Icon(Icons.Filled.Settings, contentDescription = "")
                        }
                    }
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    isSearching = true
                    searchByNo = true
                },
                containerColor = MaterialTheme.colorScheme.onPrimary
            ) { Icon(Icons.Filled.Dialpad, "Search by number") }
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            when (uiState) {
                is UiState.Filtered ->
                    SongsList(
                        songs = songs,
                        viewModel = viewModel,
                        navController = navController,
                    )
                else -> EmptyState()
            }
        }
    }

    if (searchByNo) {
        DialPad(
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
