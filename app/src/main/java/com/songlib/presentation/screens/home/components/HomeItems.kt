package com.songlib.presentation.screens.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material.Surface
import androidx.compose.material.TabRowDefaults.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.navigation.NavHostController
import com.songlib.data.models.Song
import com.songlib.presentation.components.listitems.*
import com.songlib.presentation.navigation.Routes
import com.songlib.presentation.viewmodels.HomeViewModel

@Composable
fun BooksList(viewModel: HomeViewModel) {
    val selectedBook by viewModel.selectedBook.collectAsState(initial = 0)
    val books by viewModel.books.collectAsState(initial = emptyList())

    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
        contentPadding = PaddingValues(5.dp),
        horizontalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        itemsIndexed(books) { index, book ->
            SearchBookItem(
                text = book.title,
                isSelected = selectedBook == index,
                onPressed = { viewModel.filterSongs(index) }
            )
        }
    }
}

@Composable
fun SongsList(
    songs: List<Song>,
    viewModel: HomeViewModel,
    navController: NavHostController,
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
            SearchSongItem(
                song = song,
                onClick = {
                    navController.currentBackStackEntry
                        ?.savedStateHandle
                        ?.set("song", song)
                    navController.navigate(Routes.PRESENTER)
                },
                isSelected = false,
                isSearching = false,
            )

            if (index < songs.lastIndex) {
                Divider(
                    color = MaterialTheme.colorScheme.scrim,
                    thickness = 1.dp,
                    modifier = Modifier.padding(horizontal = 15.dp, vertical = 5.dp)
                )
            }
        }
    }
}
