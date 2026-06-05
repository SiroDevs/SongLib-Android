package com.songlib.feature.presenter.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.songlib.core.ui.sample.SampleIndicators
import com.songlib.core.ui.sample.SampleVerses
import com.songlib.feature.presenter.components.PresenterIndicators
import com.songlib.feature.presenter.components.PresenterTabs

@Composable
fun PresenterContent(
    verses: List<String>,
    indicators: List<String>,
    horizontalSlides: Boolean = false,
) {
    val pagerState = rememberPagerState { verses.size }

    Column(
        modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.SpaceBetween
    ) {
        PresenterTabs(
            pagerState = pagerState, verses = verses, modifier = Modifier.weight(1f),
            horizontalSlides = horizontalSlides,
        )

        PresenterIndicators(
            pagerState = pagerState,
            indicators = indicators,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 10.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewPresenterContent() {
    PresenterContent(
        verses = SampleVerses, indicators = SampleIndicators,
    )
}
