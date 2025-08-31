package com.songlib.presentation.screens.init.step1

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.songlib.data.models.Book
import com.songlib.data.sample.SampleSelectableBooks
import com.songlib.domain.entity.*
import com.songlib.presentation.components.listitems.SongBook

@Composable
fun Step1Content(
    books: List<Selectable<Book>>,
    onBookClick: (Selectable<Book>) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(3.dp)
            .background(color = MaterialTheme.colorScheme.surface)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 5.dp)
        ) {
            items(books) { book ->
                SongBook(
                    item = book,
                    onClick = { onBookClick(book) }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun Step1ContentPreview() {
    Step1Content(
        books = SampleSelectableBooks,
        onBookClick = {},
        modifier = Modifier.fillMaxSize()
    )
}
