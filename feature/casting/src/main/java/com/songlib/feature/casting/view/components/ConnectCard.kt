package com.songlib.feature.casting.view.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.songlib.core.casting.data.HotspotStatus
import kotlinx.coroutines.launch

@Composable
fun ConnectCard(
    castingUrl: String?,
    hotspotStatus: HotspotStatus,
    wifiConnected: Boolean,
    hasExternalHotspot: Boolean,
    onStartHotspot: () -> Unit,
    onStopHotspot: () -> Unit,
    onCopyUrl: (String) -> Unit,
) {
    val hotspotRunning = hotspotStatus is HotspotStatus.Running
    val showHotspotTab = hotspotRunning || (!wifiConnected && !hasExternalHotspot)
    val showCastingTab = wifiConnected || hotspotRunning || hasExternalHotspot

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp)),
    ) {
        when {
            showHotspotTab && showCastingTab -> TwoTabConnectCard(
                castingUrl = castingUrl,
                hotspotStatus = hotspotStatus,
                onStartHotspot = onStartHotspot,
                onStopHotspot = onStopHotspot,
                onCopyUrl = onCopyUrl,
            )

            showCastingTab -> Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                CastingTab(url = castingUrl, onCopy = onCopyUrl)
            }

            else -> Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                HotspotTab(
                    hotspotStatus = hotspotStatus,
                    onStart = onStartHotspot,
                    onStop = onStopHotspot,
                )
            }
        }
    }
}

@Composable
private fun TwoTabConnectCard(
    castingUrl: String?,
    hotspotStatus: HotspotStatus,
    onStartHotspot: () -> Unit,
    onStopHotspot: () -> Unit,
    onCopyUrl: (String) -> Unit,
) {
    val tabTitles = listOf("Hotspot", "Casting")
    val pagerState = rememberPagerState(pageCount = { tabTitles.size })
    val scope = rememberCoroutineScope()

    Column {
        TabRow(
            selectedTabIndex = pagerState.currentPage,
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)),
        ) {
            tabTitles.forEachIndexed { index, title ->
                Tab(
                    selected = pagerState.currentPage == index,
                    onClick = { scope.launch { pagerState.animateScrollToPage(index) } },
                    text = { Text(title) },
                )
            }
        }

        HorizontalPager(state = pagerState) { page ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                when (page) {
                    0 -> HotspotTab(
                        hotspotStatus = hotspotStatus,
                        onStart = onStartHotspot,
                        onStop = onStopHotspot,
                    )

                    else -> CastingTab(
                        url = castingUrl,
                        onCopy = onCopyUrl,
                    )
                }
            }
        }
    }
}
