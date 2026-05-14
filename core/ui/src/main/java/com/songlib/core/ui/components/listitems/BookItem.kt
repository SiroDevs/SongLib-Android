package com.songlib.core.ui.components.listitems

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun BookItem(
    text: String,
    isSelected: Boolean = false,
    onPressed: (() -> Unit)? = null
) {
    val bgColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.inversePrimary
    val txtColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.scrim

    Button(
        onClick = { onPressed?.invoke() },
        colors = ButtonDefaults.buttonColors(
            containerColor = bgColor,
            contentColor = txtColor
        ),
        shape = RoundedCornerShape(15.dp),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 5.dp),
        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp)
    ) {
        Text(
            text = text,
            textAlign = TextAlign.Center,
            style = TextStyle(fontSize = 15.sp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewBookItem() {
    BookItem(
        text = "SongEntity of Worship",
        isSelected = false,
        onPressed = {}
    )
}