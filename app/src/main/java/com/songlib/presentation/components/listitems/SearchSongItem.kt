package com.songlib.presentation.components.listitems

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import com.songlib.core.utils.*
import com.songlib.data.models.Song
import com.songlib.presentation.theme.ThemeColors

@Composable
fun SearchSongItem(
    song: Song,
    height: Dp,
    isSelected: Boolean = false,
    isSearching: Boolean = false,
    onClick: (() -> Unit)? = null,
) {
    val verses = remember(song.content) { song.content.split("##") }
    val hasChorus = "CHORUS" in song.content
    val chorusText = if (hasChorus) "Chorus" else ""
    val verseCount = verses.size - if (hasChorus) 1 else 0
    val versesText = if (verses.size == 1) "$verseCount V" else "${verseCount} Vs"

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(if (isSelected) ThemeColors.primary else Color.Transparent)
            .clickable(onClick = { onClick?.invoke() })
            .padding(5.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = songItemTitle(song.songNo, song.title),
                //style = TextStyles.headingStyle3.copy(fontWeight = FontWeight.Bold),
                maxLines = 1,
                modifier = Modifier.weight(1f)
            )
            TagItem(tagText = versesText, height = height)
            if (hasChorus) {
                TagItem(tagText = chorusText, height = height)
            }
            Icon(
                imageVector = if (song.liked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                contentDescription = null,
                //tint = ThemeColors.foreColorBW()
            )
        }

        Spacer(modifier = Modifier.height(3.dp))

        //Divider(color = ThemeColors.foreColorPrimary2(), thickness = 1.dp)

        Row(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = refineContent(verses.firstOrNull().orEmpty()),
                //style = TextStyles.bodyStyle2.copy(lineHeight = 20.sp),
                maxLines = 2,
                modifier = Modifier.weight(1f)
            )
        }

        if (isSearching) {
            Spacer(modifier = Modifier.height(4.dp))
            //TagItem(tagText = refineTitle(song.book), height = height)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewSearchSongItem() {
    val sampleSong = Song(
        book = 1,
        songId = 2,
        songNo = 2,
        title = "Amazing Grace",
        alias = "",
        content = "Amazing Grace, How sweet the sound",
        liked = false,
        likes = 0,
        views = 0,
        created = "",
    )

    SearchSongItem(
        song = sampleSong,
        onClick = {},
        height = 50.dp,
        isSelected = false,
        isSearching = false,
    )
}
