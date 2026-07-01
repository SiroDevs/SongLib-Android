package com.songlib.feature.casting.view.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.songlib.core.common.entity.HotspotStatus
import kotlinx.coroutines.launch

@Composable
fun ConnectCard(
    castingUrl: String?,
    hotspotStatus: HotspotStatus,
    wifiConnected: Boolean,
    onStartHotspot: () -> Unit,
    onStopHotspot: () -> Unit,
    onCopyUrl: (String) -> Unit,
) {
    val hotspotRunning = hotspotStatus is HotspotStatus.Running
    val showCastingTab = wifiConnected || hotspotRunning

    Card(modifier = Modifier.fillMaxWidth()) {
        if (showCastingTab) {
            TwoTabConnectCard(
                castingUrl = castingUrl,
                hotspotStatus = hotspotStatus,
                onStartHotspot = onStartHotspot,
                onStopHotspot = onStopHotspot,
                onCopyUrl = onCopyUrl,
            )
        } else {
            Column(
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
        TabRow(selectedTabIndex = pagerState.currentPage) {
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

@Composable
private fun HotspotTab(
    hotspotStatus: HotspotStatus,
    onStart: () -> Unit,
    onStop: () -> Unit,
) {
    when (hotspotStatus) {
        is HotspotStatus.Running -> {
            Text("Scan to join — no password needed", style = MaterialTheme.typography.bodyMedium)
            QrCode(content = hotspotStatus.toWifiQrPayload(), size = 180.dp)
            Text(hotspotStatus.ssid, style = MaterialTheme.typography.titleSmall)
            OutlinedButton(onClick = onStop, modifier = Modifier.fillMaxWidth()) {
                Text("Stop Hotspot")
            }
        }

        HotspotStatus.Starting -> {
            Text("Starting hotspot…", style = MaterialTheme.typography.bodyMedium)
        }

        is HotspotStatus.Error -> {
            Text(
                "Couldn't start: ${hotspotStatus.message}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error,
            )
            Button(onClick = onStart, modifier = Modifier.fillMaxWidth()) {
                Text("Try Again")
            }
        }

        HotspotStatus.Stopped -> {
            Text(
                "Create a quick, open Wi-Fi network just for casting — it never uses your mobile data.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Button(onClick = onStart, modifier = Modifier.fillMaxWidth()) {
                Text("Start Hotspot")
            }
        }
    }
}

@Composable
private fun CastingTab(
    url: String?,
    onCopy: (String) -> Unit,
) {
    if (url == null) {
        Text(
            "Join a Wi-Fi network or start a hotspot to get a casting link.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        return
    }

    Text("Scan with your PC or any device's camera", style = MaterialTheme.typography.bodyMedium)
    QrCode(content = url, size = 180.dp)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp)),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = url,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f),
        )
        IconButton(onClick = { onCopy(url) }) {
            Icon(Icons.Default.ContentCopy, contentDescription = "Copy link")
        }
    }
}

/** A scannable Wi-Fi QR payload per the standard "WIFI:" QR format. */
private fun HotspotStatus.Running.toWifiQrPayload(): String =
    if (isOpen || password.isNullOrBlank()) {
        "WIFI:T:nopass;S:${escapeWifiQr(ssid)};;"
    } else {
        "WIFI:T:WPA;S:${escapeWifiQr(ssid)};P:${escapeWifiQr(password!!)};;"
    }

private fun escapeWifiQr(value: String): String = buildString {
    for (c in value) {
        if (c == '\\' || c == ';' || c == ',' || c == ':' || c == '"') append('\\')
        append(c)
    }
}
