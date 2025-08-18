package com.songlib.presentation.screens.presenter.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.pager.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.songlib.data.sample.*
import com.songlib.presentation.components.autosize.AutoSizeText

@Composable
fun PresenterTabs(
    pagerState: PagerState,
    verses: List<String>,
    modifier: Modifier = Modifier,
    horizontalSlides: Boolean = false,
) {
    ElevatedCard(
        modifier = modifier
            .fillMaxWidth()
            .padding(15.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.onPrimary,
        ),
        elevation = CardDefaults.cardElevation(5.dp),
    ) {
        val pager: @Composable (content: @Composable (page: Int) -> Unit) -> Unit =
            if (horizontalSlides) {
                { content ->
                    HorizontalPager(
                        state = pagerState,
                        modifier = modifier.fillMaxSize()
                    ) { page -> content(page) }
                }
            } else {
                { content ->
                    VerticalPager(
                        state = pagerState,
                        modifier = modifier.fillMaxSize()
                    ) { page -> content(page) }
                }
            }

        pager { page ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(15.dp),
                contentAlignment = Alignment.Center
            ) {
                AutoSizeText(
                    modifier = Modifier.fillMaxWidth(),
                    text = verses[page],
                    fontSize = 45.sp,
                    lineHeight = 48.sp,
                    keepLineHeight = true,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PresenterTabsPreview() {
    val pagerState = rememberPagerState { SampleVerses.size }
    LaunchedEffect(Unit) {
        pagerState.scrollToPage(0)
    }
    PresenterTabs(
        pagerState = pagerState,
        verses = SampleVerses,
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
    )
}
