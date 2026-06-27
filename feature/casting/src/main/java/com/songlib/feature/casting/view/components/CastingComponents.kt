package com.songlib.feature.casting.view.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.songlib.core.casting.model.CastingState

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
            Text("Mirror your song presentation screen", style = MaterialTheme.typography.titleMedium)
            Text(
                "Turn on this phone's Personal Hotspot (or join the same Wi-Fi as your " +
                        "PC), tap Start below, then open the link on the PC's browser. Whatever " +
                        "verse you're showing on the phone — song or draft — appears there too, " +
                        "live. When you're not on a presenter screen, it just shows a waiting page.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
fun NowShowingCard(slideState: CastingState) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text("Now showing", style = MaterialTheme.typography.labelMedium)
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
        }
    }
}
