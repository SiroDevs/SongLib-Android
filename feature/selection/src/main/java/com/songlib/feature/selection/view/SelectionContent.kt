package com.songlib.feature.selection.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.songlib.core.database.model.BookEntity
import com.songlib.core.ui.sample.SampleSelectableBooks
import com.songlib.core.common.entity.*
import com.songlib.core.ui.components.listitems.SongBook

@Composable
fun SelectionContent(
    books: List<Selectable<BookEntity>>,
    onBookClick: (Selectable<BookEntity>) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(2.dp)
            .background(color = MaterialTheme.colorScheme.surface)
    ) {
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 150.dp),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 2.dp),
            horizontalArrangement = Arrangement.spacedBy(2.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            items(books) { book ->
                SongBook(
                    item = book,
                    onClick = { onBookClick(book) },
                    modifier = Modifier.height(90.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SelectionContentPreview() {
    SelectionContent(
        books = SampleSelectableBooks,
        onBookClick = {},
        modifier = Modifier.fillMaxSize()
    )
}
