package com.songlib.feature.home.components

import android.app.Activity
import android.content.Intent
import android.speech.RecognizerIntent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.songlib.core.common.utils.Routes
import com.songlib.core.database.model.SongEntity
import com.songlib.core.ui.components.donation.DonationBanner
import com.songlib.core.ui.components.listitems.BookItem
import com.songlib.core.ui.components.listitems.SongItem
import com.songlib.feature.home.HomeViewModel
import java.util.Locale

@Composable
fun SongsList(
    songs: List<SongEntity>,
    viewModel: HomeViewModel,
    navController: NavHostController,
    selectedSongs: Set<SongEntity>,
    searchQuery: String,
    listState: LazyListState,
    onQueryChange: (String) -> Unit,
    onSongSelected: (SongEntity) -> Unit,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    showSearch: Boolean = true,
    showBookFilter: Boolean = true,
    onSearchBoxPositioned: ((Rect) -> Unit)? = null,
    onSongbooksPositioned: ((Rect) -> Unit)? = null,
    onThirdSongPositioned: ((Rect) -> Unit)? = null,
    showDonation: Boolean = false,
    onShowDonation: () -> Unit,
) {
    val selectedBook by viewModel.selectedBook.collectAsState(initial = -1)
    val books        by viewModel.books.collectAsState(initial = emptyList())

    val speechLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val spokenText = result.data
                ?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                ?.firstOrNull() ?: ""
            onQueryChange(spokenText)
        }
    }

    fun startVoiceSearch() = speechLauncher.launch(
        Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
            putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak your song!")
        }
    )

    LazyColumn(
        state = listState,
        modifier = Modifier
            .fillMaxSize()
            .padding(top = contentPadding.calculateTopPadding()),
        contentPadding = PaddingValues(
            bottom = contentPadding.calculateBottomPadding(),
            start  = contentPadding.calculateStartPadding(LayoutDirection.Ltr),
            end    = contentPadding.calculateEndPadding(LayoutDirection.Ltr),
        ),
    ) {
        // ── Search bar ────────────────────────────────────────────────────
        if (showSearch) {
            stickyHeader {
                Box(
                    modifier = Modifier.onGloballyPositioned { coords ->
                        onSearchBoxPositioned?.invoke(coords.boundsInRoot())
                    }
                ) {
                    SearchFieldRow(
                        query = searchQuery,
                        placeholder = "Search songs by title or lyrics …",
                        onQueryChange = onQueryChange,
                        onClear = { onQueryChange("") },
                        onVoiceSearch = { startVoiceSearch() },
                        onSearch = { query ->
                            // Save search when user presses the Search IME key
                            if (query.isNotBlank()) viewModel.commitSearch(query)
                        }
                    )
                }
            }
        }

        // ── Book filter chips ─────────────────────────────────────────────
        if (showBookFilter) {
            item {
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp)
                        .onGloballyPositioned { coords ->
                            onSongbooksPositioned?.invoke(coords.boundsInRoot())
                        },
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
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

        // ── Song items ────────────────────────────────────────────────────
        itemsIndexed(songs, key = { _, s -> s.songId }) { index, song ->
            if (index == 3 || index == 7) {
                DonationBanner(show = showDonation, onTap = onShowDonation)
            }

            val isSelected = selectedSongs.contains(song)
            Box(
                modifier = Modifier
                    .combinedClickable(
                        onClick = {
                            if (selectedSongs.isNotEmpty()) {
                                onSongSelected(song)
                            } else {
                                // Save the search query when tapping a result
                                if (searchQuery.isNotBlank()) viewModel.commitSearch(searchQuery)
                                // Pass book + song via savedStateHandle then navigate
                                val book = books.firstOrNull { it.bookId == song.book }
                                navController.currentBackStackEntry?.savedStateHandle?.set("book", book)
                                navController.currentBackStackEntry?.savedStateHandle?.set("song", song)
                                navController.navigate(Routes.PRESENT)
                            }
                        },
                        onLongClick = { onSongSelected(song) }
                    )
                    .background(
                        if (isSelected) MaterialTheme.colorScheme.onPrimary
                        else Color.Transparent
                    )
                    .then(
                        if (index == 2) Modifier.onGloballyPositioned { coords ->
                            onThirdSongPositioned?.invoke(coords.boundsInRoot())
                        } else Modifier
                    )
            ) {
                SongItem(song = song)
            }
        }
    }
}
