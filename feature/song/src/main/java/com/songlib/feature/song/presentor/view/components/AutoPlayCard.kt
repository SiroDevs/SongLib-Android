package com.songlib.feature.song.presentor.view.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val BadgeSize = 44.dp

@Composable
fun AutoPlayCard(
    isAutoPlaying: Boolean,
    isMonitoring: Boolean,
    elapsedSeconds: Int,
    totalSeconds: Int,
    onToggle: () -> Unit,
    onInfoClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    OutlinedCard(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            AutoPlayButton(
                onClick = onToggle,
                borderColor = MaterialTheme.colorScheme.onSurface,
            ) {
                Icon(
                    imageVector = if (isAutoPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurface,
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                if (isAutoPlaying) {
                    if (isMonitoring) {
                        Text(
                            text = "Waiting for you to move to the next Verse so",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                        )
                        Text(
                            text = "as to determine the duration per verse",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                        )
                    } else {
                        Text(
                            text = "AUTO PLAY IS ON",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                        )
                        Text(
                            text = "Moving to the next verse automatically",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                        )
                    }
                    if (!isMonitoring) {
                        val progress = if (totalSeconds > 0) {
                            (elapsedSeconds.toFloat() / totalSeconds).coerceIn(0f, 1f)
                        } else 0f
                        LinearProgressIndicator(
                            progress = { progress },
                            trackColor = Color.Transparent,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 6.dp)
                                .height(2.dp)
                                .clip(RoundedCornerShape(1.dp)),
                        )
                    }
                } else {
                    Text(
                        text = "START AUTO PLAY ON THIS SONG",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                    )
                    Text(
                        text = "Verses will move automatically",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                    )
                }
            }

            if (isAutoPlaying) {
                val badgeSeconds = if (isMonitoring) {
                    elapsedSeconds
                } else {
                    (totalSeconds - elapsedSeconds).coerceAtLeast(0)
                }
                AutoPlayTimerBadge(seconds = badgeSeconds)
            } else {
                AutoPlayButton(
                    onClick = onInfoClick,
                    borderColor = MaterialTheme.colorScheme.onSurface,
                ) {
                    Icon(
                        imageVector = Icons.Filled.Info,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurface,
                    )
                }
            }
        }
    }
}

@Composable
private fun AutoPlayButton(
    onClick: () -> Unit,
    borderColor: Color,
    content: @Composable () -> Unit,
) {
    OutlinedIconButton(
        onClick = onClick,
        modifier = Modifier.size(BadgeSize),
        shape = RoundedCornerShape(15.dp),
        border = BorderStroke(2.dp, borderColor),
    ) {
        content()
    }
}

@Composable
private fun AutoPlayTimerBadge(seconds: Int) {
    Box(
        modifier = Modifier
            .size(BadgeSize)
            .border(BorderStroke(2.dp, MaterialTheme.colorScheme.onSurface), CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = seconds.toString(),
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
        )
    }
}

@Composable
fun AutoPlayInfoDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Auto Play") },
        text = {
            Text(
                "Auto Play moves through this song's verses and chorus for you, " +
                    "so you don't have to tap or swipe while presenting.\n\n" +
                    "The first run through, it times how long you stay on each " +
                    "verse and chorus, and uses that timing to move on by itself " +
                    "after that.\n\n" +
                    "If it moves on too soon, just swipe back — it will remember " +
                    "and give that verse a little more time next time.\n\n" +
                    "You can turn Auto Play off anytime with the pause button."
            )
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Got it") }
        }
    )
}

@Preview(showBackground = true)
@Composable
private fun AutoPlayCardOffPreview() {
    AutoPlayCard(
        isAutoPlaying = false,
        isMonitoring = true,
        elapsedSeconds = 0,
        totalSeconds = 0,
        onToggle = {},
        onInfoClick = {},
        modifier = Modifier.padding(10.dp),
    )
}

@Preview(showBackground = true)
@Composable
private fun AutoPlayCardMonitoringPreview() {
    AutoPlayCard(
        isAutoPlaying = true,
        isMonitoring = true,
        elapsedSeconds = 30,
        totalSeconds = 45,
        onToggle = {},
        onInfoClick = {},
        modifier = Modifier.padding(10.dp),
    )
}

@Preview(showBackground = true)
@Composable
private fun AutoPlayCardCountingDownPreview() {
    AutoPlayCard(
        isAutoPlaying = true,
        isMonitoring = false,
        elapsedSeconds = 30,
        totalSeconds = 45,
        onToggle = {},
        onInfoClick = {},
        modifier = Modifier.padding(10.dp),
    )
}
