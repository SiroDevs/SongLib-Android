package com.songlib.feature.song.presentor.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.songlib.core.ui.sample.SampleVerses
import com.songlib.feature.song.presentor.PresenterViewModel

@Composable
fun PagerView(
    pagerState: PagerState,
    verses: List<String>,
    modifier: Modifier = Modifier,
    horizontalSlides: Boolean = false,
    fontSize: Float = PresenterViewModel.DEFAULT_FONT_SP,
    cornerOverlay: (@Composable () -> Unit)? = null,
) {
    val animatedFontSize by animateFloatAsState(
        targetValue = fontSize,
        animationSpec = tween(durationMillis = 100),
        label = "fontSize"
    )

    ElevatedCard(
        modifier = modifier
            .fillMaxWidth()
            .padding(15.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.onPrimary,
        ),
        elevation = CardDefaults.cardElevation(5.dp),
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            val pager: @Composable (content: @Composable (page: Int) -> Unit) -> Unit =
                if (horizontalSlides) {
                    { content ->
                        HorizontalPager(
                            state = pagerState,
                            modifier = Modifier.fillMaxSize()
                        ) { page -> content(page) }
                    }
                } else {
                    { content ->
                        VerticalPager(
                            state = pagerState,
                            modifier = Modifier.fillMaxSize()
                        ) { page -> content(page) }
                    }
                }

            pager { page ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = verses[page],
                        fontSize = animatedFontSize.sp,
                        fontWeight = FontWeight.Medium,
                        lineHeight = (animatedFontSize * 1.25f).sp,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                }
            }
            cornerOverlay?.invoke()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PagerPreview() {
    val pagerState = rememberPagerState { SampleVerses.size }
    LaunchedEffect(Unit) { pagerState.scrollToPage(0) }
    PagerView(
        pagerState = pagerState,
        verses = SampleVerses,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 200.dp),
    )
}