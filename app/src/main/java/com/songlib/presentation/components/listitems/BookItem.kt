package com.songlib.presentation.components.listitems

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import com.songlib.core.utils.refineTitle
import com.songlib.data.models.Book
import com.songlib.data.sample.SampleBooks
import com.songlib.domain.entity.Selectable

@Composable
fun BookItem(
    item: Selectable<Book>,
    onClick: (Selectable<Book>) -> Unit
) {
    val bgColor =
        if (item.isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.inversePrimary
    val txtColor =
        if (item.isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.scrim

    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(3.dp)
            .clickable { onClick(item) },
        colors = CardDefaults.cardColors(
            containerColor = bgColor,
            contentColor = txtColor,
        ),
        elevation = CardDefaults.cardElevation(5.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (item.isSelected) Icons.Filled.RadioButtonChecked else Icons.Filled.RadioButtonUnchecked,
                contentDescription = null,
                tint = txtColor,
                modifier = Modifier.padding(end = 12.dp)
            )
            Column {
                Text(
                    text = refineTitle(item.data.title),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = txtColor
                )
                Text(
                    text = "${item.data.songs} ${item.data.subTitle} songs",
                    fontSize = 18.sp,
                    color = txtColor
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewBookItem() {
    val sampleBook = SampleBooks[0]

    val selectableBook = Selectable(data = sampleBook, isSelected = true)

    BookItem(
        item = selectableBook,
        onClick = {}
    )
}
