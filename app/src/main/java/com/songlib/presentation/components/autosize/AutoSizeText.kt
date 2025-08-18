package com.songlib.presentation.components.autosize

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.*
import androidx.compose.ui.text.style.*
import androidx.compose.ui.unit.TextUnit

@Composable
fun AutoSizeText(
    text: String,
    modifier: Modifier = Modifier,
    minFontSize: TextUnit = TextUnit.Unspecified,
    color: Color = Color.Unspecified,
    fontSize: TextUnit = TextUnit.Unspecified,
    fontStyle: FontStyle? = null,
    fontWeight: FontWeight? = null,
    fontFamily: FontFamily? = null,
    letterSpacing: TextUnit = TextUnit.Unspecified,
    textDecoration: TextDecoration? = null,
    textAlign: TextAlign? = null,
    lineHeight: TextUnit = TextUnit.Unspecified,
    softWrap: Boolean = true,
    maxLines: Int = Int.MAX_VALUE,
    minLines: Int = 1,
    onTextLayout: (TextLayoutResult) -> Unit = {},
    style: TextStyle = LocalTextStyle.current,
    overflow: TextOverflow = TextOverflow.Clip,
    keepLineHeight: Boolean = false,
) {
    val defaultFontSize = coerceTextUnit(
        expected = fontSize,
        default = style.fontSize
    )
    val defaultLineHeight = coerceTextUnit(
        expected = lineHeight,
        default = style.lineHeight
    )

    val ratio = defaultFontSize.value / defaultLineHeight.value

    var overriddenMetrics by remember(key1 = text) {
        mutableStateOf(
            InnerMetrics(
                fontSize = defaultFontSize,
                lineHeight = defaultLineHeight
            )
        )
    }
    var textReadyToDraw by remember(key1 = text) {
        mutableStateOf(false)
    }
    var decreasingStage: SizeDecreasingStage? by remember(key1 = text) {
        mutableStateOf(null)
    }

    Text(
        modifier = modifier.drawWithContent {
            if (textReadyToDraw) {
                drawContent()
            }
        },
        text = text,
        color = color,
        textAlign = textAlign,
        fontSize = overriddenMetrics.fontSize,
        fontFamily = fontFamily,
        fontStyle = fontStyle,
        fontWeight = fontWeight,
        letterSpacing = letterSpacing,
        textDecoration = textDecoration,
        lineHeight = overriddenMetrics.lineHeight,
        style = style,
        maxLines = maxLines,
        minLines = minLines,
        softWrap = softWrap,
        overflow = if (textReadyToDraw) overflow else TextOverflow.Clip,
        onTextLayout = { result ->
            if (textReadyToDraw) {
                onTextLayout(result)
                return@Text
            }
            if (minFontSize == TextUnit.Unspecified || overriddenMetrics.fontSize > minFontSize) {
                if (result.didOverflowHeight.not() && decreasingStage == null) {
                    textReadyToDraw = true
                    onTextLayout(result)
                    return@Text
                }

                decreasingStage = decreasingStage.next(result.didOverflowHeight)
                if (decreasingStage == SizeDecreasingStage.Peace) {
                    textReadyToDraw = true
                } else {
                    val correctedFontSize = overriddenMetrics.fontSize.times(decreasingStage!!.value)
                    val correctedLineHeight =
                        if (keepLineHeight) lineHeight else correctedFontSize.div(ratio)
                    overriddenMetrics = overriddenMetrics.copy(
                        fontSize = correctedFontSize,
                        lineHeight = correctedLineHeight
                    )
                }
            } else {
                if (overriddenMetrics.fontSize <= minFontSize) {
                    val minLineHeight = if (keepLineHeight) lineHeight else minFontSize.div(ratio)
                    overriddenMetrics = InnerMetrics(
                        fontSize = minFontSize,
                        lineHeight = minLineHeight
                    )
                    textReadyToDraw = true
                }
            }
            onTextLayout(result)
        },
    )
}
