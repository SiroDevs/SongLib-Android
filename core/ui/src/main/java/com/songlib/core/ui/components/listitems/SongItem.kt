package com.songlib.core.ui.components.listitems

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.songlib.core.common.utils.refineContent
import com.songlib.core.common.utils.songItemTitle
import com.songlib.core.database.model.SongEntity
import com.songlib.core.ui.sample.SampleSongs

@Composable
fun SongItem(song: SongEntity) {
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
                .padding(vertical = 2.dp)
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
                        fontSize = 14.sp,
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

            Row(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = refineContent(verses.firstOrNull().orEmpty()),
                    style = TextStyle(fontSize = 14.sp),
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
