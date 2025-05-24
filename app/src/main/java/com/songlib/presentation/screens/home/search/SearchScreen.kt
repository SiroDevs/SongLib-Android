package com.songlib.presentation.screens.home.search

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material.Surface
import androidx.compose.material.TabRowDefaults.Divider
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.songlib.data.models.*
import com.songlib.domain.entities.UiState
import com.songlib.presentation.components.EmptyState
import com.songlib.presentation.components.listitems.*
import com.songlib.presentation.navigation.Routes
import com.songlib.presentation.theme.ThemeColors
import com.songlib.presentation.viewmodels.HomeViewModel

@Composable
fun SearchScreen(
    viewModel: HomeViewModel,
    navController: NavHostController
) {
    val selectedBook = 0
    val uiState by viewModel.uiState.collectAsState()
    val books by viewModel.books.collectAsState(initial = emptyList())
    val filtered by viewModel.songs.collectAsState(initial = emptyList())

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        when (uiState) {
            is UiState.Filtered ->
                SearchList(
                    navController = navController,
                    books = books,
                    songs = filtered,
                    selectedBook = selectedBook,
                    onBookSelected = { viewModel.filterSongs(books.indexOf(it)) },
                )

            else -> EmptyState()
        }
    }
}

@Composable
fun SearchList(
    navController: NavHostController,
    books: List<Book>,
    songs: List<Song>,
    selectedBook: Int = 0,
    onBookSelected: (Book) -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
    ) {
        stickyHeader {
            Surface(
                color = ThemeColors.accent,
                modifier = Modifier
                    .fillMaxWidth()
                    .zIndex(1f)
            ) {
                BooksList(
                    books = books,
                    selectedBook = selectedBook,
                    onBookSelected = onBookSelected,
                )
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
                    color = Color.LightGray,
                    thickness = 1.dp,
                    modifier = Modifier.padding(horizontal = 15.dp, vertical = 5.dp)
                )
            }
        }
    }
}

@Composable
fun BooksList(
    books: List<Book>,
    selectedBook: Int = 0,
    onBookSelected: (Book) -> Unit
) {
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
                onPressed = { onBookSelected(book) }
            )
        }
    }
}
