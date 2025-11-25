package com.songlib.presentation.components.listitems

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.*
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import com.songlib.core.utils.refineTitle
import com.songlib.data.models.Book
import com.songlib.data.sample.SampleBooks
import com.songlib.domain.entity.Selectable
@Composable
fun SongBook(
    item: Selectable<Book>,
    onClick: (Selectable<Book>) -> Unit,
    modifier: Modifier = Modifier
) {
    val bgColor =
        if (item.isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.inversePrimary
    val txtColor =
        if (item.isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.scrim

    ElevatedCard(
        modifier = modifier
            .fillMaxWidth()
            .padding(2.dp)
            .clickable { onClick(item) },
        colors = CardDefaults.cardColors(
            containerColor = bgColor,
            contentColor = txtColor,
        ),
        elevation = CardDefaults.cardElevation(5.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(5.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = buildAnnotatedString {
                    withStyle(style = SpanStyle(fontSize = 16.sp, color = txtColor)) {
                        append(refineTitle(item.data.title))
                    }
                    append(" ")
                    withStyle(style = SpanStyle(fontSize = 12.sp, color = txtColor.copy(alpha = 0.7f))) {
                        append("(${item.data.songs})")
                    }
                },
                maxLines = 3
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewSongBook() {
    val sampleBook = SampleBooks[0]

    val selectableBook = Selectable(data = sampleBook, isSelected = true)

    SongBook(
        item = selectableBook,
        onClick = {}
    )
}
