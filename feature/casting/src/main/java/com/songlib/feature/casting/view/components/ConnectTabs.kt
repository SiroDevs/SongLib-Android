package com.songlib.feature.casting.view.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.songlib.core.casting.data.HotspotStatus
import com.songlib.core.ui.components.action.QrCode

@Composable
fun HotspotTab(
    hotspotStatus: HotspotStatus,
    onStart: () -> Unit,
    onStop: () -> Unit,
) {
    when (hotspotStatus) {
        is HotspotStatus.Running -> {
            Text("Scan to join", style = MaterialTheme.typography.bodyLarge)
            QrCode(content = hotspotStatus.toWifiQrPayload(), size = 180.dp)
            Text("Name: ${hotspotStatus.ssid}", style = MaterialTheme.typography.titleMedium)
            Text("Password: ${hotspotStatus.password}", style = MaterialTheme.typography.titleMedium)
            OutlinedButton(onClick = onStop, modifier = Modifier.fillMaxWidth()) {
                Text("Stop Hotspot")
            }
        }

        HotspotStatus.Starting -> {
            Text("Starting hotspot ...", style = MaterialTheme.typography.bodyMedium)
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
fun CastingTab(
    url: String?,
    onCopy: (String) -> Unit,
) {
    if (url == null) {
        Text(
            "Join a Wi-Fi network or start a hotspot to produce a casting link.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        return
    }

    Text("Scan with your Device's camera", style = MaterialTheme.typography.bodyLarge)
    QrCode(content = url, size = 180.dp)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp)),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "Address: $url",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f),
        )
        IconButton(onClick = { onCopy(url) }) {
            Icon(Icons.Default.ContentCopy, contentDescription = "Copy link")
        }
    }
}

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
