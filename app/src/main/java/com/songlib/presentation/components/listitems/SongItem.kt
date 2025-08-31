package com.songlib.presentation.components.listitems

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import com.songlib.core.utils.*
import com.songlib.data.models.Song
import com.songlib.data.sample.SampleSongs

@Composable
fun SongItem(song: Song) {
    val verses = remember(song.content) { song.content.split("##") }
    val hasChorus = "CHORUS" in song.content
    val chorusText = if (hasChorus) "Chorus" else ""
    val verseCount = verses.size - if (hasChorus) 1 else 0
    val versesText = if (verses.size == 1) "$verseCount v" else "$verseCount vs"

    Box(
        modifier = Modifier.padding(horizontal = 10.dp)
    )
    {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 5.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = songItemTitle(song.songNo, song.title),
                    maxLines = 1,
                    modifier = Modifier.weight(1f),
                    color = MaterialTheme.colorScheme.scrim,
                    style = TextStyle(
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
                TagItem(tagText = versesText)
                if (hasChorus) {
                    Spacer(modifier = Modifier.width(3.dp))
                    TagItem(tagText = chorusText)
                }
                Spacer(modifier = Modifier.width(3.dp))
                Icon(
                    imageVector = if (song.liked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = null,
                )
            }

            Spacer(modifier = Modifier.height(3.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = refineContent(verses.firstOrNull().orEmpty()),
                    style = TextStyle(fontSize = 16.sp),
                    maxLines = 2,
                    modifier = Modifier.weight(1f),
                    color = MaterialTheme.colorScheme.scrim,
                )
            }
//        if (isSearching) {
//            Spacer(modifier = Modifier.height(4.dp))
//            TagItem(tagText = refineTitle(song.book), height = height)
//        }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewSongItem() {
    SongItem(
        song = SampleSongs[3],
    )
}
