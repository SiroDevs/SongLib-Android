package com.songlib.feature.home.components

import androidx.compose.animation.core.EaseInOutSine
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.songlib.feature.home.R
import kotlin.math.roundToInt

enum class DemoStep { SEARCH_BOX, SONGBOOKS, SONG_ITEM, FAB_BUTTON, DONE }

data class DemoTargetBounds(
    val searchBox: Rect = Rect.Zero,
    val songbooks: Rect = Rect.Zero,
    val songItem: Rect = Rect.Zero,
    val fabButton: Rect = Rect.Zero,
)

private data class DemoStepContent(
    val title: String,
    val explanation: String,
    val drawableRes: Int,
    val rect: Rect,
)

@Composable
fun DemoOverlay(
    isVisible: Boolean,
    targetBounds: DemoTargetBounds,
    onDismiss: () -> Unit,
) {
    if (!isVisible) return

    var currentStep by remember { mutableStateOf(DemoStep.SEARCH_BOX) }

    if (currentStep == DemoStep.DONE) {
        onDismiss()
        return
    }

    val glowAlpha by rememberInfiniteTransition(label = "glow").animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "glowAlpha",
    )

    val steps = mapOf(
        DemoStep.SEARCH_BOX to DemoStepContent(
            title = "Search Box",
            explanation = "Search for a song by its title or its lyrics",
            drawableRes = R.drawable.search_box,
            rect = targetBounds.searchBox,
        ),
        DemoStep.SONGBOOKS to DemoStepContent(
            title = "Songbooks Filter",
            explanation = "Activate which book you want to search songs in",
            drawableRes = R.drawable.song_books,
            rect = targetBounds.songbooks,
        ),
        DemoStep.SONG_ITEM to DemoStepContent(
            title = "Song Item",
            explanation = "Long press a song to manage it — like, share, or add to a listing",
            drawableRes = R.drawable.song_item,
            rect = targetBounds.songItem,
        ),
        DemoStep.FAB_BUTTON to DemoStepContent(
            title = "Number Search",
            explanation = "Tap here to search a song by its number using the dial pad",
            drawableRes = R.drawable.fab_button,
            rect = targetBounds.fabButton,
        ),
    )

    val step = steps[currentStep] ?: return
    val highlightRect = step.rect
    val isFirst = currentStep == DemoStep.SEARCH_BOX
    val isLast = currentStep == DemoStep.FAB_BUTTON

    val ordered = listOf(
        DemoStep.SEARCH_BOX, DemoStep.SONGBOOKS, DemoStep.SONG_ITEM, DemoStep.FAB_BUTTON,
    )
    val idx = ordered.indexOf(currentStep)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = {},
            )
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawIntoCanvas { canvas ->
                canvas.drawRect(
                    left = 0f, top = 0f,
                    right = size.width, bottom = size.height,
                    paint = Paint().apply { color = Color.Black.copy(alpha = 0.72f) },
                )
                if (highlightRect != Rect.Zero) {
                    val pad = 12f
                    canvas.drawRoundRect(
                        left = highlightRect.left - pad,
                        top = highlightRect.top - pad,
                        right = highlightRect.right + pad,
                        bottom = highlightRect.bottom + pad,
                        radiusX = 16f, radiusY = 16f,
                        paint = Paint().apply { blendMode = BlendMode.Clear },
                    )
                }
            }
        }

        if (highlightRect != Rect.Zero) {
            val density = LocalDensity.current
            val imageTop = with(density) {
                (highlightRect.top - highlightRect.height - 12.dp.toPx())
                    .coerceAtLeast(8.dp.toPx())
                    .roundToInt()
            }

            Image(
                painter = painterResource(id = step.drawableRes),
                contentDescription = step.title,
                contentScale = ContentScale.FillWidth,
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .offset { IntOffset(0, imageTop) }
                    .shadow(
                        elevation = (24 * glowAlpha).dp,
                        spotColor = Color(0xFFFFD700),
                        ambientColor = Color(0xFFFFD700),
                    ),
            )
        }

        val density = LocalDensity.current
        val cardOffsetY = if (highlightRect != Rect.Zero &&
            highlightRect.bottom > 0.6f * with(density) { 700.dp.toPx() }
        ) {
            with(density) { (highlightRect.top - 240.dp.toPx()).roundToInt() }
        } else {
            with(density) { (highlightRect.bottom + 28.dp.toPx()).roundToInt() }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .offset {
                    IntOffset(
                        0,
                        cardOffsetY.coerceAtLeast(with(density) { 80.dp.roundToPx() })
                    )
                }
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth(),
            ) {
                DemoStepLabel(text = step.title)
                Spacer(Modifier.height(8.dp))
                DemoStepExplanation(text = step.explanation)
                Spacer(Modifier.height(20.dp))
                DemoNavButtons(
                    isFirst = isFirst,
                    isLast = isLast,
                    onPrevious = { currentStep = ordered[idx - 1] },
                    onNext = { currentStep = if (isLast) DemoStep.DONE else ordered[idx + 1] },
                    onClose = onDismiss,
                )
            }
        }
    }
}
