package com.songlib.core.ui.components.listitems

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun TagItem(tagText: String) {
    if (tagText.isNotEmpty()) {
        Box(
            modifier = Modifier
                .background(
                    color = MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(5.dp)
                )
                .padding(horizontal = 5.dp, vertical = 2.dp)
        ) {
            Text(
                text = tagText,
                style = TextStyle(
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onPrimary,
                    letterSpacing = 1.sp,
                    fontStyle = FontStyle.Italic
                )
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
fun PreviewTagItem() {
    TagItem(tagText = "Chorus")
}