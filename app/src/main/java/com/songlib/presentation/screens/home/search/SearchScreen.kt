package com.songlib.presentation.screens.home.search

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.songlib.data.models.Book
import com.songlib.data.models.Song
import com.songlib.data.sample.*
import com.songlib.domain.entities.UiState
import com.songlib.presentation.components.EmptyState
import com.songlib.presentation.components.listitems.*
import com.songlib.presentation.viewmodels.HomeViewModel

@Composable
fun SearchScreen(
    viewModel: HomeViewModel,
) {
    val selectedBook = 0
    val uiState by viewModel.uiState.collectAsState()
    val books by viewModel.books.collectAsState(initial = emptyList())
    val filtered by viewModel.songs.collectAsState(initial = emptyList())

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {

        when (uiState) {
            is UiState.Filtered ->
                SearchList(
                    books = books,
                    songs = filtered,
                    selectedBook = selectedBook,
                    onBookSelected = {}
                )

            else -> EmptyState()
        }
    }
}

@Composable
fun SearchList(
    books: List<Book>,
    songs: List<Song>,
    selectedBook: Int = 0,
    onBookSelected: (Book) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = PaddingValues(horizontal = 10.dp)
    ) {
        item {
            BooksList(
                books = books,
                selectedBook = selectedBook,
                onBookSelected = {},
            )
        }

        items(songs) { song ->
            SearchSongItem(
                song = song,
                onClick = { },
                height = 50.dp,
                isSelected = false,
                isSearching = false,
            )
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
            .height(40.dp),
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

@Preview(showBackground = true)
@Composable
fun PreviewSearchList() {
    SearchList(
        books = SampleBooks,
        songs = SampleSongs,
        selectedBook = 0,
        onBookSelected = {}
    )
}