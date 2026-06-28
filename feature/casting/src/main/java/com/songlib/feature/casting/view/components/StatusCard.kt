package com.songlib.feature.casting.view.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.songlib.core.common.entity.CastingState
import com.songlib.core.common.entity.ServerStatus

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
                        ServerStatus.Stopped -> "Not broadcasting"
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
                    "Waiting page (no presenter open)",
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
