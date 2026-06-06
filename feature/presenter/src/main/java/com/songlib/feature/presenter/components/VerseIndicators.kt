package com.songlib.feature.presenter.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.songlib.core.ui.sample.SampleIndicators
import com.songlib.core.ui.sample.SampleVerses
import kotlinx.coroutines.launch

@Composable
fun VerseIndicators(
    pagerState: PagerState,
    indicators: List<String>,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()

    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 50.dp),
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 10.dp, end = 70.dp),
        verticalArrangement = Arrangement.spacedBy(5.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = PaddingValues(8.dp)
    ) {
        items(indicators.size) { index ->
            val label = indicators[index]
            val isSelected = pagerState.currentPage == index
            val bgColor =
                if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.inversePrimary
            val txtColor =
                if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.scrim

            Button(
                onClick = {
                    scope.launch { pagerState.animateScrollToPage(index) }
                },
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = bgColor,
                    contentColor = txtColor,
                ),
                elevation = ButtonDefaults.buttonElevation(5.dp),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 5.dp),
                modifier = Modifier
                    .height(40.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = label,
                    style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun VerseIndicatorsPreview() {
    val pagerState = rememberPagerState { SampleVerses.size }
    LaunchedEffect(Unit) {
        pagerState.scrollToPage(0)
    }

    VerseIndicators(
        pagerState = pagerState,
        indicators = SampleIndicators,
        modifier = Modifier.padding(16.dp)
    )
}
