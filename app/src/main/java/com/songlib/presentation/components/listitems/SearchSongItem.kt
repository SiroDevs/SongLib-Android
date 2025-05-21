package com.songlib.presentation.components.listitems

import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import com.songlib.core.utils.refineContent
import com.songlib.core.utils.refineTitle
import com.songlib.core.utils.songItemTitle
import com.songlib.data.models.Book
import com.songlib.data.models.Song
import com.songlib.domain.entities.Selectable
import com.songlib.presentation.theme.ThemeColors

@Composable
fun SearchSongItem(
    song: Song,
    height: Dp,
    isSelected: Boolean = false,
    isSearching: Boolean = false,
    onTap: (() -> Unit)? = null,
    onDoubleTap: (() -> Unit)? = null
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
            .clickable(onClick = { onTap?.invoke() })
            .pointerInput(Unit) {
                detectTapGestures(
                    onDoubleTap = { onDoubleTap?.invoke() }
                )
            }
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
    val sampleBook = Book(
        bookId = 1,
        bookNo = 1,
        created = "2023-01-01",
        enabled = true,
        position = 1,
        songs = 12,
        subTitle = "worship",
        title = "Songs of Worship",
        user = 42
    )

    val selectableBook = Selectable(data = sampleBook, isSelected = true)

    BookItem(
        item = selectableBook,
        onClick = {}
    )
}
