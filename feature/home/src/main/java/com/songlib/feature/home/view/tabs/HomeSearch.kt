package com.songlib.feature.home.view.tabs

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dialpad
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
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
import androidx.navigation.NavHostController
import com.songlib.core.common.entity.UiState
import com.songlib.core.common.utils.Routes
import com.songlib.feature.home.HomeViewModel
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.songlib.core.database.model.SongEntity
import com.songlib.core.ui.components.action.SearchTopBar
import com.songlib.core.ui.components.indicators.EmptyState
import com.songlib.feature.home.components.DialPad
import com.songlib.feature.home.components.HomeSearchAppBar
import com.songlib.feature.home.components.SongsList

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeSearch(
    viewModel: HomeViewModel,
    navController: NavHostController,
) {
    val uiState by viewModel.uiState.collectAsState()
    var isSearching by rememberSaveable { mutableStateOf(false) }
    var searchByNo by rememberSaveable { mutableStateOf(false) }
    var searchQry by rememberSaveable { mutableStateOf("") }
    val songs by viewModel.filtered.collectAsState(initial = emptyList())
    var selectedSongs by remember { mutableStateOf<Set<SongEntity>>(emptySet()) }

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
                HomeSearchAppBar(
                    viewModel = viewModel,
                    selectedSongs = selectedSongs,
                    onSearchClick = { isSearching = true },
                    onSettingsClick = { navController.navigate(Routes.SETTINGS) },
                    onShareClick = { },
                    onClearSelection = { selectedSongs = emptySet() }
                )
            }
        },
        floatingActionButton = {
            if (selectedSongs.isEmpty()) {
                FloatingActionButton(
                    onClick = {
                        isSearching = true
                        searchByNo = true
                    },
                    containerColor = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier
                        .navigationBarsPadding()
                        .padding(bottom = 30.dp)
                ) { Icon(Icons.Filled.Dialpad, "SearchEntity by number") }
            }
        },
    ) { Box(
            modifier = Modifier
                .padding(it)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            when (uiState) {
                is UiState.Filtered ->
                    SongsList(
                        songs = songs,
                        viewModel = viewModel,
                        navController = navController,
                        selectedSongs = selectedSongs,
                        onSongSelected = { song ->
                            selectedSongs =
                                if (selectedSongs.contains(song)) selectedSongs - song
                                else selectedSongs + song
                        }
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
