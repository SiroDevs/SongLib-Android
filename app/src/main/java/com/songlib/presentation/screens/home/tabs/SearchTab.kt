package com.songlib.presentation.screens.home.tabs

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.Surface
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.TabRowDefaults.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.songlib.domain.entity.UiState
import com.songlib.presentation.components.listitems.*
import com.songlib.presentation.navigation.Routes
import com.songlib.presentation.viewmodels.HomeViewModel
import com.swahilib.presentation.components.indicators.EmptyState

@Composable
fun SearchTab(
    viewModel: HomeViewModel,
    navController: NavHostController
) {
    val uiState by viewModel.uiState.collectAsState()

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        when (uiState) {
            is UiState.Filtered ->
                SearchList(
                    viewModel = viewModel,
                    navController = navController,
                )

            else -> EmptyState()
        }
    }
}

@Composable
fun SearchList(
    viewModel: HomeViewModel,
    navController: NavHostController,
) {
    val songs by viewModel.filtered.collectAsState(initial = emptyList())

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
    ) {
        stickyHeader {
            Surface(
                color = Color.Transparent,
                modifier = Modifier
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

@Composable
fun BooksList(
    viewModel: HomeViewModel,
) {
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
