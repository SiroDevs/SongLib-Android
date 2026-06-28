package com.songlib.feature.history.view.screen

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.songlib.core.common.utils.Routes
import com.songlib.core.ui.components.action.AppTopBar
import com.songlib.feature.history.viewmodel.HistoryViewModel
import com.songlib.feature.history.view.tabs.SearchesTab
import com.songlib.feature.history.view.tabs.ViewsTab
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    navController: NavHostController,
    viewModel: HistoryViewModel = hiltViewModel(),
) {
    val tabs = listOf("Views", "Searches")
    val pagerState = rememberPagerState(pageCount = { tabs.size })
    val scope = rememberCoroutineScope()

    val views by viewModel.views.collectAsState()
    val searches by viewModel.searches.collectAsState()
    val bookMap by viewModel.bookMap.collectAsState()
    val selectedViews by viewModel.selectedViewIds.collectAsState()
    val selectedSearchIds by viewModel.selectedSearchIds.collectAsState()
    val showOlderViews by viewModel.showOlderViews.collectAsState()
    val showOlderSearches by viewModel.showOlderSearches.collectAsState()

    LaunchedEffect(Unit) { viewModel.load() }

    val currentPage = pagerState.currentPage
    val hasViewSelection = currentPage == 0 && selectedViews.isNotEmpty()
    val hasSearchSelection = currentPage == 1 && selectedSearchIds.isNotEmpty()

    val topBarTitle = when {
        hasViewSelection -> "${selectedViews.size} selected"
        hasSearchSelection -> "${selectedSearchIds.size} selected"
        else -> "History"
    }

    Scaffold(
        topBar = {
            AppTopBar(
                title = topBarTitle,
                showGoBack = true,
                onNavIconClick = {
                    when {
                        hasViewSelection -> viewModel.clearViewSelection()
                        hasSearchSelection -> viewModel.clearSearchSelection()
                        else -> navController.popBackStack()
                    }
                },
                actions = {
                    if (hasViewSelection) {
                        IconButton(onClick = { viewModel.deleteSelectedViews() }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete selected")
                        }
                    } else if (hasSearchSelection) {
                        IconButton(onClick = { viewModel.deleteSelectedSearches() }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete selected")
                        }
                    }
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
            PrimaryTabRow(
                selectedTabIndex = pagerState.currentPage,
                containerColor = MaterialTheme.colorScheme.onPrimary,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            ) {
                tabs.forEachIndexed { i, title ->
                    Tab(
                        selected = pagerState.currentPage == i,
                        onClick = {
                            if (i == 0) viewModel.clearSearchSelection()
                            else viewModel.clearViewSelection()
                            scope.launch { pagerState.animateScrollToPage(i) }
                        },
                        text = { Text(title) }
                    )
                }
            }

            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.Top,
            ) { page ->
                when (page) {
                    0 -> ViewsTab(
                        views = views,
                        bookMap = bookMap,
                        selectedIds = selectedViews,
                        showOlder = showOlderViews,
                        onToggleOlder = { viewModel.toggleOlderViews() },
                        onItemClick = { songView ->
                            if (selectedViews.isNotEmpty()) {
                                viewModel.toggleViewSelection(songView.song.id)
                            } else {
                                navController.currentBackStackEntry
                                    ?.savedStateHandle?.set("song", songView.entity)
                                navController.navigate(Routes.PRESENT)
                            }
                        },
                        onItemLongClick = { songView -> viewModel.toggleViewSelection(songView.song.id) }
                    )

                    1 -> SearchesTab(
                        searches = searches,
                        selectedIds = selectedSearchIds,
                        showOlder = showOlderSearches,
                        onToggleOlder = { viewModel.toggleOlderSearches() },
                        onItemClick = { search ->
                            if (selectedSearchIds.isNotEmpty()) {
                                viewModel.toggleSearchSelection(search.id)
                            }
                        },
                        onItemLongClick = { search -> viewModel.toggleSearchSelection(search.id) }
                    )
                }
            }
        }
    }
}