package com.songlib.feature.home.view

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.songlib.core.common.utils.Routes
import com.songlib.core.database.model.BookEntity
import com.songlib.core.database.model.SearchEntity
import com.songlib.core.database.model.SongEntity
import com.songlib.core.ui.components.action.AppTopBar
import com.songlib.core.ui.components.indicators.EmptyState
import com.songlib.core.ui.components.listitems.SongItem
import com.songlib.feature.home.HistoryViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    navController: NavHostController,
    viewModel: HistoryViewModel = hiltViewModel(),
) {
    val tabs  = listOf("Views", "Searches")
    val pagerState = rememberPagerState(pageCount = { tabs.size })
    val scope = rememberCoroutineScope()

    val views             by viewModel.views.collectAsState()
    val searches          by viewModel.searches.collectAsState()
    val bookMap           by viewModel.bookMap.collectAsState()
    val selectedViews     by viewModel.selectedViewIds.collectAsState()
    val selectedSearchIds by viewModel.selectedSearchIds.collectAsState()

    LaunchedEffect(Unit) { viewModel.load() }

    val currentPage = pagerState.currentPage
    val hasViewSelection   = currentPage == 0 && selectedViews.isNotEmpty()
    val hasSearchSelection = currentPage == 1 && selectedSearchIds.isNotEmpty()

    val topBarTitle = when {
        hasViewSelection   -> "${selectedViews.size} selected"
        hasSearchSelection -> "${selectedSearchIds.size} selected"
        else               -> "History"
    }

    Scaffold(
        topBar = {
            AppTopBar(
                title = topBarTitle,
                showGoBack = true,
                onNavIconClick = {
                    when {
                        hasViewSelection   -> viewModel.clearViewSelection()
                        hasSearchSelection -> viewModel.clearSearchSelection()
                        else               -> navController.popBackStack()
                    }
                },
                actions = {
                    // Delete selected items
                    if (hasViewSelection) {
                        IconButton(onClick = { viewModel.deleteSelectedViews() }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete selected")
                        }
                    } else if (hasSearchSelection) {
                        IconButton(onClick = { viewModel.deleteSelectedSearches() }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete selected")
                        }
                    }
                    // Clear all in the active tab
                    IconButton(onClick = {
                        if (currentPage == 0) viewModel.clearViews()
                        else viewModel.clearSearches()
                    }) {
                        Icon(Icons.Default.DeleteSweep, contentDescription = "Clear all")
                    }
                }
            )
        }
    ) { padding ->
        Column(Modifier.padding(padding)) {
            TabRow(selectedTabIndex = pagerState.currentPage) {
                tabs.forEachIndexed { i, title ->
                    Tab(
                        selected = pagerState.currentPage == i,
                        onClick = {
                            // Clear selection when switching tabs
                            if (i == 0) viewModel.clearSearchSelection()
                            else viewModel.clearViewSelection()
                            scope.launch { pagerState.animateScrollToPage(i) }
                        },
                        text = { Text(title, modifier = Modifier.padding(vertical = 12.dp)) }
                    )
                }
            }

            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize(),
            ) { page ->
                when (page) {
                    0 -> ViewsTab(
                        views = views,
                        bookMap = bookMap,
                        selectedIds = selectedViews,
                        onItemClick = { song ->
                            if (selectedViews.isNotEmpty()) {
                                viewModel.toggleViewSelection(song.songId)
                            } else {
                                navController.currentBackStackEntry
                                    ?.savedStateHandle?.set("song", song)
                                navController.navigate(Routes.PRESENT)
                            }
                        },
                        onItemLongClick = { song -> viewModel.toggleViewSelection(song.songId) }
                    )
                    1 -> SearchesTab(
                        searches = searches,
                        selectedIds = selectedSearchIds,
                        onItemClick = { search ->
                            if (selectedSearchIds.isNotEmpty()) {
                                viewModel.toggleSearchSelection(search.id)
                            }
                            // Could optionally pre-fill the search bar here via a callback
                        },
                        onItemLongClick = { search -> viewModel.toggleSearchSelection(search.id) }
                    )
                }
            }
        }
    }
}

// ── Views tab ─────────────────────────────────────────────────────────────────

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ViewsTab(
    views: List<SongEntity>,
    bookMap: Map<Int, BookEntity>,
    selectedIds: Set<Int>,
    onItemClick: (SongEntity) -> Unit,
    onItemLongClick: (SongEntity) -> Unit,
) {
    if (views.isEmpty()) {
        EmptyState(message = "No songs viewed yet")
    } else {
        LazyColumn {
            items(views, key = { it.songId }) { song ->
                val bookTitle  = bookMap[song.book]?.title ?: ""
                val isSelected = song.songId in selectedIds
                Box(
                    modifier = Modifier
                        .combinedClickable(
                            onClick    = { onItemClick(song) },
                            onLongClick = { onItemLongClick(song) }
                        )
                        .background(
                            if (isSelected) MaterialTheme.colorScheme.primaryContainer
                            else Color.Transparent
                        )
                ) {
                    SongItem(
                        song = song,
                        showLike = false,
                        // title    → "1. Only Believe"
                        customTitle = "${song.songNo}. ${song.title}",
                        // subtitle → songbook name
                        customSubtitle = bookTitle,
                    )
                }
                HorizontalDivider()
            }
        }
    }
}

// ── Searches tab ──────────────────────────────────────────────────────────────

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun SearchesTab(
    searches: List<SearchEntity>,
    selectedIds: Set<Int>,
    onItemClick: (SearchEntity) -> Unit,
    onItemLongClick: (SearchEntity) -> Unit,
) {
    if (searches.isEmpty()) {
        EmptyState(message = "No searches yet")
    } else {
        LazyColumn {
            items(searches, key = { it.id }) { search ->
                val isSelected = search.id in selectedIds
                Box(
                    modifier = Modifier
                        .combinedClickable(
                            onClick    = { onItemClick(search) },
                            onLongClick = { onItemLongClick(search) }
                        )
                        .background(
                            if (isSelected) MaterialTheme.colorScheme.primaryContainer
                            else Color.Transparent
                        )
                ) {
                    ListItem(
                        headlineContent = { Text(search.title) },
                        trailingContent = {
                            Text(
                                text  = "${search.hits} hit${if (search.hits != 1) "s" else ""}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary,
                            )
                        }
                    )
                }
                HorizontalDivider()
            }
        }
    }
}
