package com.songlib.presentation.screens.presentor

import android.R.attr.padding
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.songlib.core.utils.songItemTitle
import com.songlib.data.models.Song
import com.songlib.domain.entities.UiState
import com.songlib.presentation.components.EmptyState
import com.songlib.presentation.components.ErrorState
import com.songlib.presentation.components.LoadingState
import com.songlib.presentation.screens.home.HomeContent
import com.songlib.presentation.screens.home.search.SearchList
import com.songlib.presentation.viewmodels.PresentorViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PresentorScreen(
    viewModel: PresentorViewModel,
    navController: NavHostController,
    onBackPressed: () -> Unit,
    song: Song?,
) {
    LaunchedEffect(song) {
        song?.let { viewModel.loadSong(it) }
    }

    val uiState by viewModel.uiState.collectAsState()
    when (uiState) {
        is UiState.Error -> Scaffold { padding ->
            Box(
                modifier = Modifier.padding(padding)
            ) {
                ErrorState(
                    errorMessage = (uiState as UiState.Error).errorMessage,
                    onRetry = { }
                )
            }
        }

        UiState.Loading -> Scaffold { padding ->
            Box(
                modifier = Modifier.padding(padding)
            ) { LoadingState("Loading song ...") }
        }

        else -> Scaffold { padding ->
            Box(
                modifier = Modifier.padding(padding)
            ) { EmptyState() }
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PresentorContent(
    viewModel: PresentorViewModel,
) {
    val title by viewModel.title.collectAsState()
    val hasChorus by viewModel.hasChorus.collectAsState()
    val indicators by viewModel.indicators.collectAsState()
    val verses by viewModel.verses.collectAsState()

    Scaffold(
        topBar = {
            Surface(shadowElevation = 3.dp) {
                TopAppBar(
                    title = { Text(title) },
                    actions = {
                        IconButton(onClick = { }) {
                            Icon(
                                imageVector = Icons.Filled.Favorite,
                                contentDescription = "Like"
                            )
                        }
                    },
                )
            }
        },
        content = {
            Box(
                modifier = Modifier
                    .padding(it)
                    .fillMaxSize()
                    .background(color = MaterialTheme.colorScheme.surface)
            ) {
                EmptyState()
            }
        }
    )

}