package com.songlib.presentation.components.autosize

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun AutoResizingText(
    text: String,
    modifier: Modifier = Modifier,
    maxFontSize: TextUnit = 48.sp,
    minFontSize: TextUnit = 16.sp
) {
    BoxWithConstraints(modifier = modifier) {
        var textStyle by remember { mutableStateOf(TextStyle(fontSize = maxFontSize)) }
        var readyToDraw by remember { mutableStateOf(false) }

        Text(
            text = text,
            style = textStyle,
            maxLines = Int.MAX_VALUE,
            softWrap = true,
            onTextLayout = { result ->
                if (result.didOverflowHeight && textStyle.fontSize > minFontSize) {
                    textStyle = textStyle.copy(fontSize = textStyle.fontSize * 0.9)
                } else {
                    readyToDraw = true
                }
            },
            modifier = Modifier.alpha(if (readyToDraw) 1f else 0f)
        )
    }
}
