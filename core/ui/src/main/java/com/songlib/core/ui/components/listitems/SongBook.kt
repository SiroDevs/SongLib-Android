package com.songlib.core.ui.components.listitems

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.songlib.core.common.entity.Selectable
import com.songlib.core.common.utils.refineTitle
import com.songlib.core.database.model.BookEntity
import com.songlib.core.ui.components.indicators.ShimmerBrush
import com.songlib.core.ui.sample.SampleSelectableBooks

@Composable
fun SongBook(
    item: Selectable<BookEntity>,
    onClick: (Selectable<BookEntity>) -> Unit,
    modifier: Modifier = Modifier,
) {
    val selected = item.isSelected
    val book = item.data

    val containerColor by animateColorAsState(
        targetValue = if (selected) MaterialTheme.colorScheme.primary
        else MaterialTheme.colorScheme.surfaceVariant,
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label = "containerColor",
    )
    val onContainerColor by animateColorAsState(
        targetValue = if (selected) MaterialTheme.colorScheme.onPrimary
        else MaterialTheme.colorScheme.onSurfaceVariant,
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label = "onContainerColor",
    )
    val borderColor by animateColorAsState(
        targetValue = if (selected) MaterialTheme.colorScheme.primary
        else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f),
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label = "borderColor",
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 5.dp, vertical = 5.dp)
            .clip(RoundedCornerShape(10.dp))
            .border(
                width = if (selected) 2.dp else 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(10.dp),
            )
            .background(
                brush = if (selected) {
                    Brush.horizontalGradient(
                        listOf(
                            MaterialTheme.colorScheme.primary,
                            MaterialTheme.colorScheme.primaryContainer,
                        )
                    )
                } else {
                    Brush.horizontalGradient(
                        listOf(containerColor, containerColor)
                    )
                }
            )
            .clickable { onClick(item) }
            .padding(horizontal = 10.dp, vertical = 10.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text       = refineTitle(book.title),
                    fontSize   = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color      = if (selected) onContainerColor
                    else MaterialTheme.colorScheme.onSurface,
                    maxLines   = 2,
                    overflow   = TextOverflow.Ellipsis,
                )
                Spacer(Modifier.height(4.dp))
                SongCountChip(
                    count    = book.songs,
                    selected = selected,
                    textColor = onContainerColor,
                )
            }

            if (selected) {
                Icon(
                    imageVector        = Icons.Default.CheckCircle,
                    contentDescription = "Selected",
                    tint               = onContainerColor,
                    modifier           = Modifier.size(22.dp),
                )
            }
        }
    }
}

@Composable
private fun SongCountChip(count: Int, selected: Boolean, textColor: Color) {
    Surface(
        shape = RoundedCornerShape(50),
        color = if (selected) textColor.copy(alpha = 0.18f)
        else MaterialTheme.colorScheme.primary.copy(alpha = 0.10f),
    ) {
        Text(
            text       = "$count Songs",
            fontSize   = 12.sp,
            fontWeight = FontWeight.Medium,
            color      = if (selected) textColor
            else MaterialTheme.colorScheme.primary,
            modifier   = Modifier.padding(horizontal = 15.dp, vertical = 3.dp),
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFBFF)
@Composable
fun PreviewSongBook() {
    MaterialTheme {
        Column {
            SampleSelectableBooks.forEach { book ->
                SongBook(item = book, onClick = {})
            }
        }
    }
}