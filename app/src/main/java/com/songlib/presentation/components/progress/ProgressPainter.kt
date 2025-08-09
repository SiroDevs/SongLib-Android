package com.songlib.presentation.components.progress

import androidx.compose.ui.graphics.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.*
import kotlin.math.*

fun DrawScope.drawPrimaryProgress(
    primaryValue: Float?,
    secondaryValue: Float?,
    secondaryWidth: Float,
    radius: Float,
    startAngle: Float,
    maxDegrees: Float,
    progressGap: Float,
    division: Int,
    levelAmount: Int?,
    levelLowWidth: Float,
    levelLowHeight: Float,
    levelHighWidth: Float,
    levelHighHeight: Float,
    levelHighBeginEnd: Boolean,
    primaryColor: Color,
    secondaryColor: Color,
    tertiaryColor: Color
) {
    if (primaryValue == null || levelAmount == null) return

    val secondarySpace = if (secondaryValue != null) secondaryWidth + progressGap else 0f
    val extraSpace = max(levelHighHeight, levelLowHeight) + secondarySpace
    val activeRadius = radius - extraSpace
    val anglePerItem = maxDegrees / levelAmount

    for (index in 0 until levelAmount) {
        val angle = anglePerItem * index + startAngle + (anglePerItem / 2f)
        val isFillWithColor = (index.toFloat() / levelAmount) <= primaryValue && primaryValue != 0f

        val isHighLevel = if (
            division > 0 &&
            ((index > 0 && index < levelAmount - 1) ||
                    (levelHighBeginEnd && index == 0) ||
                    (levelHighBeginEnd && index == levelAmount - 1))
        ) {
            index % division == 0
        } else false

        val color = if (isFillWithColor) {
            lerp(primaryColor, secondaryColor, index.toFloat() / levelAmount)
        } else {
            tertiaryColor
        }

        val strokeWidth = if (isHighLevel) levelHighWidth else levelLowWidth
        val height = if (isHighLevel) levelHighHeight else levelLowHeight

        val radAngle = Math.toRadians(angle.toDouble())
        val offset = Offset(
            (activeRadius * cos(Math.PI * angle / 180) + activeRadius + extraSpace).toFloat(),
            (activeRadius * sin(Math.PI * angle / 180) + activeRadius + extraSpace).toFloat()
        )

        withTransform({
            translate(offset.x, offset.y)
            rotate(angle)
        }) {
            drawLine(
                color = color,
                start = Offset.Zero,
                end = Offset(height, 0f),
                strokeWidth = strokeWidth,
                cap = StrokeCap.Round
            )
        }
    }
}

fun DrawScope.drawSecondaryProgress(
    secondaryValue: Float?,
    secondaryWidth: Float,
    radius: Float,
    startAngle: Float,
    maxDegrees: Float,
    primaryColor: Color,
    secondaryColor: Color,
    tertiaryColor: Color
) {
    if (secondaryValue == null) return

    val halfWidth = secondaryWidth / 2
    val rect = Rect(
        left = halfWidth,
        top = halfWidth,
        right = size.width - secondaryWidth,
        bottom = size.height - secondaryWidth
    )

    drawArc(
        color = tertiaryColor,
        startAngle = startAngle,
        sweepAngle = maxDegrees,
        useCenter = false,
        style = Stroke(width = secondaryWidth, cap = StrokeCap.Round),
        topLeft = rect.topLeft,
        size = Size(rect.width, rect.height)
    )

    val shader = Brush.sweepGradient(
        colors = listOf(primaryColor, secondaryColor),
        center = rect.center
    )

    drawArc(
        brush = shader,
        startAngle = startAngle,
        sweepAngle = maxDegrees * secondaryValue,
        useCenter = false,
        style = Stroke(width = secondaryWidth, cap = StrokeCap.Round),
        topLeft = rect.topLeft,
        size = Size(rect.width, rect.height)
    )
}
