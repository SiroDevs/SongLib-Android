package com.songlib.presentation.screens.presenter.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.pager.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.tooling.preview.Preview
import com.songlib.data.sample.*
import kotlinx.coroutines.launch
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

@Composable
fun PresenterIndicators(
    pagerState: PagerState,
    indicators: List<String>,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()

    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 65.dp),
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp),
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
fun PresenterIndicatorsPreview() {
    val pagerState = rememberPagerState { SampleVerses.size }
    LaunchedEffect(Unit) {
        pagerState.scrollToPage(0)
    }

    PresenterIndicators(
        pagerState = pagerState,
        indicators = SampleIndicators,
        modifier = Modifier.padding(16.dp)
    )
}
