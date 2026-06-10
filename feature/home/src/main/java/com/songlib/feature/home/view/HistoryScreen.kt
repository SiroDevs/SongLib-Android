package com.songlib.feature.home.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.songlib.core.common.utils.Routes
import com.songlib.core.database.model.SongEntity
import com.songlib.core.ui.components.action.AppTopBar
import com.songlib.core.ui.components.indicators.EmptyState
import com.songlib.feature.home.HistoryViewModel

@Composable
fun HistoryScreen(
    navController: NavHostController,
    viewModel: HistoryViewModel = hiltViewModel(),
) {
    val tabs = listOf("Views", "Searches")
    var selectedTab by remember { mutableIntStateOf(0) }
    val views    by viewModel.views.collectAsState()
    val searches by viewModel.searches.collectAsState()

    LaunchedEffect(Unit) { viewModel.load() }

    Scaffold(
        topBar = {
            AppTopBar(
                title      = "History",
                showGoBack = true,
                onNavIconClick = { navController.popBackStack() }
            )
        }
    ) { padding ->
        Column(Modifier.padding(padding)) {
            TabRow(selectedTabIndex = selectedTab) {
                tabs.forEachIndexed { i, title ->
                    Tab(
                        selected = selectedTab == i,
                        onClick  = { selectedTab = i },
                        text     = { Text(title, modifier = Modifier.padding(vertical = 12.dp)) }
                    )
                }
            }

            when (selectedTab) {
                0 -> {
                    if (views.isEmpty()) {
                        EmptyState(message = "No song views yet")
                    } else {
                        LazyColumn {
                            items(views, key = { it.songId }) { song ->
                                SongHistoryItem(
                                    song    = song,
                                    onClick = {
                                        navController.currentBackStackEntry
                                            ?.savedStateHandle?.set("song", song)
                                        navController.navigate(Routes.PRESENTER)
                                    }
                                )
                            }
                        }
                    }
                }
                1 -> {
                    if (searches.isEmpty()) {
                        EmptyState(message = "No searches yet")
                    } else {
                        LazyColumn {
                            items(searches, key = { it.id }) { search ->
                                ListItem(
                                    headlineContent = { Text(search.title) },
                                    trailingContent = {
                                        Text(
                                            text  = "${search.hits} hit${if (search.hits != 1) "s" else ""}",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                )
                                HorizontalDivider()
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SongHistoryItem(song: SongEntity, onClick: () -> Unit) {
    ListItem(
        headlineContent   = { Text(song.title, maxLines = 1) },
        supportingContent = { Text("No. ${song.songNo}") },
        modifier          = Modifier.clickable(onClick = onClick)
    )
    HorizontalDivider()
}
