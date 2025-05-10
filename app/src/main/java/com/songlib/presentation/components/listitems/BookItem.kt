package com.songlib.presentation.components.listitems

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
    onPressed: (() -> Unit)? = null
) {
    val backgroundColor = if (item.isSelected) ThemeColors.primary else Color.White
    val contentColor = if (item.isSelected) Color.White else ThemeColors.primaryDark

    Card(
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 5.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp, horizontal = 8.dp)
            .clickable { onPressed?.invoke() }
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
