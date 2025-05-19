package com.songlib.presentation.screens.selection.step1

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.songlib.data.models.Book
import com.songlib.domain.entities.*
import com.songlib.presentation.components.*
import com.songlib.presentation.components.listitems.BookItem

@Composable
fun Step1Content(
    uiState: UiState,
    books: List<Selectable<Book>>,
    onRetry: () -> Unit,
    onBookClick: (Selectable<Book>) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(3.dp)
            .background(color = MaterialTheme.colorScheme.surface)
    ) {
        when (uiState) {
            is UiState.Error -> ErrorState(
                errorMessage = uiState.errorMessage,
                onRetry = onRetry
            )

            is UiState.Loading -> LoadingState("Loading books ...")
            is UiState.Saving -> LoadingState("Saving books ...")
            is UiState.Loaded -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(5.dp),
                    contentPadding = PaddingValues(horizontal = 5.dp)
                ) {
                    items(books) { book ->
                        BookItem(
                            item = book,
                            onClick = { onBookClick(book) }
                        )
                    }
                }
            }

            else -> EmptyState()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun Step1ContentPreview() {
    val books = listOf(
        Selectable(
            Book(
                bookId = 1,
                bookNo = 1,
                created = "",
                enabled = true,
                position = 1,
                songs = 750,
                subTitle = "worship",
                title = "Songs of Worship",
                user = 1
            ),
        ),
        Selectable(
            Book(
                bookId = 2,
                bookNo = 2,
                created = "",
                enabled = true,
                position = 2,
                songs = 220,
                subTitle = "injili",
                title = "Nyimbo za Injili",
                user = 1
            ),
            isSelected = true
        ),
        Selectable(
            Book(
                bookId = 3,
                bookNo = 3,
                created = "",
                enabled = true,
                position = 2,
                songs = 600,
                subTitle = "kikuyu",
                title = "Nyimbo cia Kunira Ngai",
                user = 1
            ),
            isSelected = true
        ),
        Selectable(
            Book(
                bookId = 4,
                bookNo = 4,
                created = "",
                enabled = true,
                position = 4,
                songs = 200,
                subTitle = "gusii",
                title = "Amatero Y'enchiri",
                user = 1
            ),
            isSelected = false
        ),
    )

    Step1Content(
        uiState = UiState.Loaded,
        books = books,
        onRetry = {},
        onBookClick = {},
        modifier = Modifier.fillMaxSize()
    )
}
