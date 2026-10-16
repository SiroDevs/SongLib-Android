package com.songlib.feature.song.presentor.view.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Formatted card rendered only to be captured into a bitmap for sharing — it is never
 * shown directly to the user, it's drawn off-screen and turned into an image.
 */
@Composable
private fun ShareCardTemplate(
    label: String,
    content: String,
    contentStyle: TextStyle,
    songTitle: String,
    bookName: String?,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.widthIn(min = 300.dp, max = 340.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(0.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
    ) {
        Column(
            modifier = Modifier.padding(28.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Text(
                text = label.uppercase(),
                style = MaterialTheme.typography.labelMedium.copy(
                    letterSpacing = 2.sp,
                    fontWeight = FontWeight.Bold,
                ),
                color = MaterialTheme.colorScheme.primary,
            )

            Text(
                text = content.trim(),
                style = contentStyle,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
            )

            HorizontalDivider(color = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))

            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = songTitle,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                )
                if (!bookName.isNullOrBlank()) {
                    Text(
                        text = bookName,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.75f),
                    )
                }
            }

            Text(
                text = "SongLib",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
                modifier = Modifier.align(Alignment.End),
            )
        }
    }
}

/** Single-verse share card: big centered verse text, song title + book name as a footer. */
@Composable
fun VerseShareCard(
    verseLabel: String,
    verseText: String,
    songTitle: String,
    bookName: String?,
    modifier: Modifier = Modifier,
) {
    ShareCardTemplate(
        label = verseLabel,
        content = verseText,
        contentStyle = MaterialTheme.typography.headlineSmall.copy(
            fontWeight = FontWeight.Medium,
            lineHeight = 32.sp,
        ),
        songTitle = songTitle,
        bookName = bookName,
        modifier = modifier,
    )
}

/** Whole-song share card: full lyrics at a readable size, song title + book name as a footer. */
@Composable
fun SongShareCard(
    songTitle: String,
    bookName: String?,
    lyrics: String,
    modifier: Modifier = Modifier,
) {
    ShareCardTemplate(
        label = "Full Song",
        content = lyrics,
        contentStyle = MaterialTheme.typography.bodyLarge.copy(lineHeight = 24.sp),
        songTitle = songTitle,
        bookName = bookName,
        modifier = modifier,
    )
}
