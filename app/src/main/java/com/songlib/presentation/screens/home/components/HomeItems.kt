package com.songlib.presentation.screens.home.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.songlib.presentation.components.listitems.*
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
            BookItem(
                text = book.title,
                isSelected = selectedBook == index,
                onPressed = { viewModel.filterSongs(index) }
            )
        }
    }
}
