package com.songlib.presentation.screens.presentor

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.songlib.data.models.Song
import com.songlib.presentation.viewmodels.PresentorViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PresentorScreen(
    viewModel: PresentorViewModel,
    navController: NavHostController,
    onBackPressed: () -> Unit,
    song: Song?,
) {
    var loadSong by rememberSaveable { mutableStateOf(0) }

    if (loadSong == 0) {
        viewModel.loadSong()
        loadSong = loadSong.inc()
    }

    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            Surface(shadowElevation = 3.dp) {
                TopAppBar(
                    title = { Text("Song") },
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

            }
        }
    )
}