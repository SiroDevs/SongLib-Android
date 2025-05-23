package com.songlib.presentation.screens.presentor.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.pager.*
import kotlinx.coroutines.launch

@Composable
fun PresentorTabs(
    verses: List<String>,
    indicators: List<String>
) {
    val pagerState = rememberPagerState { verses.size }
    val scope = rememberCoroutineScope()

    Column(modifier = Modifier.fillMaxSize()) {

        // Tabs for verse indicators
        ScrollableTabRow(
            selectedTabIndex = pagerState.currentPage,
            edgePadding = 0.dp
        ) {
            indicators.forEachIndexed { index, label ->
                Tab(
                    selected = pagerState.currentPage == index,
                    onClick = {
                        scope.launch {
                            pagerState.animateScrollToPage(index)
                        }
                    },
                    text = { Text(label) }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Swipeable pager
        HorizontalPager(
            pageCount = verses.size,
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.TopStart
            ) {
                Text(
                    text = verses[page],
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}