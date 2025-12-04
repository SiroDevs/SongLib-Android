package com.songlib.presentation.listing.view

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.songlib.data.models.Song
import com.songlib.presentation.components.listitems.*
import com.songlib.presentation.navigation.Routes

@Composable
fun ListedSongs(
    songs: List<Song>,
    navController: NavHostController,
    selectedSongs: Set<Song>,
    onSongSelected: (Song) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
    ) {
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
                        if (isSelected) MaterialTheme.colorScheme.onPrimary
                        else Color.Transparent
                    )
            ) {
                SongItem(song = song)
            }
            HorizontalDivider(
                color = MaterialTheme.colorScheme.surfaceVariant,
                thickness = 1.dp,
            )
        }
    }
}
