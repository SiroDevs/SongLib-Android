package com.songlib.feature.casting.view.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Card
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

@Composable
fun ConnectCard(
    urls: List<String>,
    onCopy: (String) -> Unit,
    onOpenHotspotSettings: () -> Unit,
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text("Open on your PC", style = MaterialTheme.typography.titleSmall)

            if (urls.isEmpty()) {
                Text(
                    "No network link detected yet. Turn on Personal Hotspot, or join the " +
                            "same Wi-Fi network as your PC, then reopen this screen.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            } else {
                urls.forEach { url ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { onCopy(url) },
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = url,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier
                                .weight(1f)
                                .padding(vertical = 8.dp),
                        )
                        IconButton(onClick = { onCopy(url) }) {
                            Icon(Icons.Default.ContentCopy, contentDescription = "Copy link")
                        }
                    }
                }
            }

            OutlinedButton(onClick = onOpenHotspotSettings, modifier = Modifier.fillMaxWidth()) {
                Icon(Icons.Default.Wifi, contentDescription = null)
                Spacer(Modifier.size(8.dp))
                Text("Open Hotspot Settings")
            }
        }
    }
}
