package com.songlib.presentation.screens.home.components

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material.Surface
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.zIndex
import androidx.navigation.NavHostController
import com.songlib.data.models.Song
import com.songlib.presentation.components.listitems.*
import com.songlib.presentation.navigation.Routes
import com.songlib.presentation.viewmodels.HomeViewModel

@Composable
fun SongsList(
    songs: List<Song>,
    viewModel: HomeViewModel,
    navController: NavHostController,
    selectedSongs: Set<Song>,
    onSongSelected: (Song) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
    ) {
        stickyHeader {
            Surface(
                color = Color.Transparent,
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.onSecondary)
                    .fillMaxWidth()
                    .zIndex(1f)
            ) {
                BooksList(viewModel = viewModel)
            }
        }

        itemsIndexed(songs) { index, song ->
            val isSelected = selectedSongs.contains(song)

            Box(
                modifier = Modifier
                    .combinedClickable(
                        onClick = {
                            if (selectedSongs.isNotEmpty()) {
                                onSongSelected(song)
                            } else {
                                navController.currentBackStackEntry
                                    ?.savedStateHandle
                                    ?.set("song", song)
                                navController.navigate(Routes.PRESENTER)
                            }
                        },
                        onLongClick = {
                            onSongSelected(song)
                        }
                    )
                    .background(
                        if (isSelected) MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f)
                        else Color.Transparent
                    )
            ) {
                SongItem(song = song)
            }
        }
    }
}
