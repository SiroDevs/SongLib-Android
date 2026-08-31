package com.songlib.feature.song.presentor.view.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.songlib.core.common.utils.AppFonts
import com.songlib.core.ui.sample.SampleIndicators
import com.songlib.core.ui.sample.SampleVerses
import kotlinx.coroutines.flow.SharedFlow

@Composable
fun PresenterLayers(
    verses: List<String>,
    indicators: List<String>,
    songTitle: String,
    bookName: String?,
    horizontalSlides: Boolean = false,
    fontSize: Float = AppFonts.DEFAULT_FONT_SP,
    cornerOverlay: (@Composable () -> Unit)? = null,
    onVerseIndexChanged: (Int) -> Unit = {},
    autoAdvanceTo: SharedFlow<Int>? = null,
    isAutoPlaying: Boolean = false,
    autoPlayElapsedSeconds: Int = 0,
    autoPlayTotalSeconds: Int = 0,
    onToggleAutoPlay: () -> Unit = {},
) {
    val pagerState = rememberPagerState { verses.size }
    var showAutoPlayInfo by remember { mutableStateOf(false) }

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }.collect { page ->
            onVerseIndexChanged(page)
        }
    }

    LaunchedEffect(autoAdvanceTo) {
        autoAdvanceTo?.collect { page ->
            if (page in verses.indices) {
                pagerState.animateScrollToPage(page)
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        AutoPlayCard(
            isAutoPlaying = isAutoPlaying,
            elapsedSeconds = autoPlayElapsedSeconds,
            totalSeconds = autoPlayTotalSeconds,
            onToggle = onToggleAutoPlay,
            onInfoClick = { showAutoPlayInfo = true },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp, start = 10.dp, end = 10.dp),
        )

        PagerView(
            pagerState = pagerState,
            verses = verses,
            songTitle = songTitle,
            bookName = bookName,
            indicators = indicators,
            modifier = Modifier.weight(1f),
            horizontalSlides = horizontalSlides,
            fontSize = fontSize,
            cornerOverlay = cornerOverlay,
        )

        VerseIndicators(
            pagerState = pagerState,
            indicators = indicators,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 10.dp)
        )
    }

    if (showAutoPlayInfo) {
        AutoPlayInfoDialog(onDismiss = { showAutoPlayInfo = false })
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewPresenterContent() {
    PresenterLayers(
        verses = SampleVerses,
        indicators = SampleIndicators,
        songTitle = "Sample Song",
        bookName = "Sample Book",
    )
}