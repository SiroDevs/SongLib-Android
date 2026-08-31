package com.songlib.feature.song.presentor.view.components

import android.os.Build
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.songlib.core.ui.components.share.ShareHelper
import com.songlib.core.ui.components.share.captureOnly
import kotlinx.coroutines.launch

/**
 * Share and Copy buttons shown at the bottom of each verse slide. Copy puts the verse
 * text (with the song title and book name at the bottom) on the clipboard. Share builds
 * a formatted image of the verse and opens the Android share sheet directly.
 */
@Composable
fun VerseShareButtons(
    songTitle: String,
    bookName: String?,
    verseLabel: String,
    verseText: String,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var capturing by remember { mutableStateOf(false) }
    val shareGraphicsLayer = rememberGraphicsLayer()

    val shareText = remember(songTitle, bookName, verseLabel, verseText) {
        buildString {
            append(verseText.trim())
            append("\n\n")
            append(songTitle)
            if (!bookName.isNullOrBlank()) append(" · ").append(bookName)
            append("\n\nvia SongLib https://songlib.vercel.app")
        }
    }

    Box(modifier = modifier) {
        // Measured, placed, and drawn normally (so the graphics layer has content to
        // capture) but reports zero size and is invisible, so it takes no space here.
        Box(modifier = Modifier.captureOnly()) {
            VerseShareCard(
                verseLabel = verseLabel,
                verseText = verseText,
                songTitle = songTitle,
                bookName = bookName,
                modifier = Modifier.drawWithContent {
                    shareGraphicsLayer.record { this@drawWithContent.drawContent() }
                    drawLayer(shareGraphicsLayer)
                },
            )
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            OutlinedButton(
                onClick = {
                    ShareHelper.copyText(context, shareText)
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
                        Toast.makeText(context, "Verse copied to clipboard", Toast.LENGTH_SHORT)
                            .show()
                    }
                },
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                ),
            ) {
                Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(6.dp))
                Text("Copy")
            }

            OutlinedButton(
                onClick = {
                    if (capturing) return@OutlinedButton
                    capturing = true
                    scope.launch {
                        try {
                            val bitmap = shareGraphicsLayer.toImageBitmap().asAndroidBitmap()
                            ShareHelper.shareBitmap(
                                context = context,
                                bitmap = bitmap,
                                fileName = "songlib_${
                                    songTitle.take(20).replace(" ", "_")
                                }_${verseLabel.replace(" ", "_")}.png",
                            )
                        } finally {
                            capturing = false
                        }
                    }
                },
                enabled = !capturing,
            ) {
                Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(6.dp))
                Text(if (capturing) "…" else "Share")
            }
        }
    }
}
