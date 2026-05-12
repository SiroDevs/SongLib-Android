package com.songlib.feature.home.components

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.songlib.core.database.model.SongEntity
import com.songlib.core.ui.components.listitems.*
import com.songlib.core.common.utils.Routes
import com.songlib.feature.home.HomeViewModel

@Composable
fun SongsList(
    songs: List<SongEntity>,
    viewModel: HomeViewModel,
    navController: NavHostController,
    selectedSongs: Set<SongEntity>,
    onSongSelected: (SongEntity) -> Unit
) {
    val selectedBook by viewModel.selectedBook.collectAsState(initial = 0)
    val books by viewModel.books.collectAsState(initial = emptyList())

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
    ) {
        item {
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(35.dp),
                contentPadding = PaddingValues(5.dp),
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                itemsIndexed(books) { index, book ->
                    BookItem(
                        text = book.title,
                        isSelected = selectedBook == index,
                        onPressed = { viewModel.filterSongs(index) }
                    )
                }
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
                        if (isSelected) MaterialTheme.colorScheme.onPrimary
                        else Color.Transparent
                    )
            ) {
                SongItem(song = song)
            }
            Divider(
                color = MaterialTheme.colorScheme.surfaceVariant,
                thickness = 1.dp,
            )
        }
    }
}
