package com.songlib.feature.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.songlib.core.database.model.SongEntity
import com.songlib.core.common.utils.Routes
import com.songlib.core.ui.components.listitems.BookItem
import com.songlib.core.ui.components.listitems.SongItem
import com.songlib.feature.home.HomeViewModel

@Composable
fun SongsList(
    songs: List<SongEntity>,
    viewModel: HomeViewModel,
    navController: NavHostController,
    selectedSongs: Set<SongEntity>,
    searchQuery: String,
    onQueryChange: (String) -> Unit,
    onSongSelected: (SongEntity) -> Unit,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    showSearch: Boolean = true,
    showBookFilter: Boolean = true,
) {
    val selectedBook by viewModel.selectedBook.collectAsState(initial = -1)
    val books by viewModel.books.collectAsState(initial = emptyList())

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = contentPadding.calculateTopPadding()),
        contentPadding = PaddingValues(
            bottom = contentPadding.calculateBottomPadding(),
            start = contentPadding.calculateStartPadding(LayoutDirection.Ltr),
            end = contentPadding.calculateEndPadding(LayoutDirection.Ltr),
        ),
    ) {
        if (showSearch){
            stickyHeader {
                SearchBox(
                    searchQuery = searchQuery,
                    onQueryChange = onQueryChange,
                )
            }
        }

        if (showBookFilter) {
            item {
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    item {
                        BookItem(
                            text = "All",
                            isSelected = selectedBook == -1,
                            onPressed = { viewModel.filterSongs(-1) }
                        )
                    }
                    itemsIndexed(books) { index, book ->
                        BookItem(
                            text = book.title,
                            isSelected = selectedBook == index,
                            onPressed = { viewModel.filterSongs(index) }
                        )
                    }
                }
            }
        }

        itemsIndexed(songs) { _, song ->
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
                        onLongClick = { onSongSelected(song) }
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