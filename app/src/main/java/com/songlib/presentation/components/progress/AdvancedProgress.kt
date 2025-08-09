package com.songlib.presentation.components.progress

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.*
import androidx.compose.ui.unit.Dp

@Composable
fun AdvancedProgress(
    radius: Float,
    primaryValue: Float? = null,
    secondaryValue: Float? = null,
    secondaryWidth: Float = 10f,
    startAngle: Float = 120f,
    maxDegrees: Float = 300f,
    progressGap: Float = 0f,
    division: Int = 10,
    levelAmount: Int? = null,
    levelLowWidth: Float = 1f,
    levelLowHeight: Float = 8f,
    levelHighWidth: Float = 2f,
    levelHighHeight: Float = 16f,
    levelHighBeginEnd: Boolean = false,
    primaryColor: Color = Color.Yellow,
    secondaryColor: Color = Color.Red,
    tertiaryColor: Color = Color.LightGray,
    child: @Composable (() -> Unit)? = null
) {
    val sizePx = radius * 2

    Box(
        modifier = Modifier.size(Dp(sizePx / 1f)),
        contentAlignment = androidx.compose.ui.Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(Dp(sizePx / 1f))) {
            drawPrimaryProgress(
                primaryValue,
                secondaryValue,
                secondaryWidth,
                radius,
                startAngle,
                maxDegrees,
                progressGap,
                division,
                levelAmount,
                levelLowWidth,
                levelLowHeight,
                levelHighWidth,
                levelHighHeight,
                levelHighBeginEnd,
                primaryColor,
                secondaryColor,
                tertiaryColor
            )
            drawSecondaryProgress(
                secondaryValue,
                secondaryWidth,
                radius,
                startAngle,
                maxDegrees,
                primaryColor,
                secondaryColor,
                tertiaryColor
            )
        }
        child?.invoke()
    }
}
