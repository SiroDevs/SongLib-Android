package com.songlib.feature.casting.view.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.songlib.core.casting.model.CastingState
import com.songlib.core.casting.model.ServerStatus

@Composable
fun StatusCard(
    serverStatus: ServerStatus,
    connectedClients: Int,
    slideState: CastingState,
    onStart: () -> Unit,
    onStop: () -> Unit,
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                StatusDot(running = serverStatus is ServerStatus.Running)
                Spacer(Modifier.size(8.dp))
                Text(
                    text = when (serverStatus) {
                        ServerStatus.Stopped -> "Not casting"
                        ServerStatus.Starting -> "Starting ..."
                        is ServerStatus.Running -> "Casting" +
                                if (connectedClients > 0) " • $connectedClients connected" else ""
                        is ServerStatus.Error -> "Couldn't start: ${serverStatus.message}"
                    },
                    style = MaterialTheme.typography.titleSmall,
                )
            }

            when (slideState) {
                CastingState.Idle -> Text(
                    "Waiting ... (presentation is open)",
                    style = MaterialTheme.typography.bodyMedium,
                )

                is CastingState.Slide -> Text(
                    "${slideState.title} — slide ${slideState.currentIndex + 1} of ${slideState.verses.size}",
                    style = MaterialTheme.typography.bodyMedium,
                )
            }

            if (serverStatus is ServerStatus.Running || serverStatus is ServerStatus.Starting) {
                Button(onClick = onStop, modifier = Modifier.fillMaxWidth()) {
                    Text("Stop Casting")
                }
            } else {
                Button(onClick = onStart, modifier = Modifier.fillMaxWidth()) {
                    Text("Start Casting")
                }
            }

        }
    }
}

@Composable
fun StatusDot(running: Boolean) {
    val color = if (running) Color(0xFF22C55E) else MaterialTheme.colorScheme.outline
    Box(
        modifier = Modifier
            .size(10.dp)
            .clip(CircleShape)
            .background(color)
    )
}

@Composable
fun ExplainerCard() {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text("Mirror Your Song Presentation", style = MaterialTheme.typography.titleMedium)
            Text(
                "Start a Hotspot or join the same Wi-Fi as your " +
                        "PC or nearby devices. Tap Start Casting, then open the link on the a device's browser.\n\n" +
                        "Whatever song or draft you're view on this phone — appears there too, " +
                        "live. When you're not, it just shows a waiting page.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

