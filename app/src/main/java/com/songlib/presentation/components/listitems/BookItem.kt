package com.songlib.presentation.components.listitems

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import com.songlib.core.utils.refineTitle
import com.songlib.data.models.Book
import com.songlib.domain.entities.Selectable
import com.songlib.presentation.theme.ThemeColors

@Composable
fun BookItem(
    item: Selectable<Book>,
    onClick: (Selectable<Book>) -> Unit
) {
//    val backgroundColor = if (item.isSelected) ThemeColors.primary else Color.White
    val contentColor = if (item.isSelected) ThemeColors.primary else Color.Black

    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp)
            .clickable { onClick(item) },
//        colors = CardDefaults.cardColors(
//            containerColor = ThemeColors.primary,
//            contentColor = contentColor,
//            disabledContainerColor = ThemeColors.primary,
//            disabledContentColor = contentColor
//        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 5.dp
        ),
    ) {
        Box(
//            modifier = Modifier.background(backgroundColor)
        ) {
            ListItem(
                leadingContent = {
                    Icon(
                        imageVector = if (item.isSelected) Icons.Filled.RadioButtonChecked else Icons.Filled.RadioButtonUnchecked,
                        contentDescription = null,
                        tint = contentColor,
                        modifier = Modifier.padding(5.dp)
                    )
                },
                headlineContent = {
                    Text(
                        text = refineTitle(item.data.title),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = contentColor
                    )
                },
                supportingContent = {
                    Text(
                        text = "${item.data.songs} ${item.data.subTitle} songs",
                        fontSize = 18.sp,
                        color = contentColor
                    )
                },
            )
        }
    }
}

//@Preview(showBackground = true)
//@Composable
//fun PreviewBookItem() {
//    val sampleBook = Book(
//        bookId = 1,
//        bookNo = 1,
//        created = "2023-01-01",
//        enabled = true,
//        position = 1,
//        songs = 12,
//        subTitle = "worship",
//        title = "Songs of Worship",
//        user = 42
//    )
//
//    val selectableBook = Selectable(data = sampleBook, isSelected = true)
//
//    BookItem(
//        item = selectableBook,
//        onClick = {}
//    )
//}
