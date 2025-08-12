package com.songlib.presentation.screens.presenter.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.pager.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.songlib.data.sample.*

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
            PresenterSlide(text = verses[page])
        }
    }
}

@Composable
fun PresenterSlide(text: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = TextStyle(
                fontSize = 25.sp,
                letterSpacing = 1.sp,
                fontWeight = FontWeight.Bold,
            ),
            modifier = Modifier.fillMaxWidth()
        )
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
