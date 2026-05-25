package com.songlib.feature.home.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import kotlin.math.roundToInt

enum class DemoStep {
    SEARCH_BOX,
    SONGBOOKS,
    SONG_ITEM,
    FAB_BUTTON,
    DONE
}

data class DemoTargetBounds(
    val searchBox: Rect = Rect.Zero,
    val songbooks: Rect = Rect.Zero,
    val songItem: Rect = Rect.Zero,
    val fabButton: Rect = Rect.Zero,
)

@Composable
fun DemoOverlay(
    isVisible: Boolean,
    targetBounds: DemoTargetBounds,
    onDismiss: () -> Unit,
) {
    if (!isVisible) return

    var currentStep by remember { mutableStateOf(DemoStep.SEARCH_BOX) }

    val glowAlpha by rememberInfiniteTransition(label = "glow").animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowAlpha"
    )

    val highlightRect = when (currentStep) {
        DemoStep.SEARCH_BOX -> targetBounds.searchBox
        DemoStep.SONGBOOKS -> targetBounds.songbooks
        DemoStep.SONG_ITEM -> targetBounds.songItem
        DemoStep.FAB_BUTTON -> targetBounds.fabButton
        DemoStep.DONE -> Rect.Zero
    }

    val stepTitle = when (currentStep) {
        DemoStep.SEARCH_BOX -> "Search Box"
        DemoStep.SONGBOOKS -> "Songbooks Filter"
        DemoStep.SONG_ITEM -> "Song Item"
        DemoStep.FAB_BUTTON -> "Number Search"
        DemoStep.DONE -> ""
    }

    val stepExplanation = when (currentStep) {
        DemoStep.SEARCH_BOX -> "Search for a song by its title or its lyrics"
        DemoStep.SONGBOOKS -> "Activate which book you want to search songs in"
        DemoStep.SONG_ITEM -> "Long press a song to manage it — like, share, or add to a listing"
        DemoStep.FAB_BUTTON -> "Tap here to search a song by its number using the dial pad"
        DemoStep.DONE -> ""
    }

    if (currentStep == DemoStep.DONE) {
        onDismiss()
        return
    }

    // Full-screen scrim with a cutout
    Box(
        modifier = Modifier
            .fillMaxSize()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = {} // consume taps
            )
    ) {
        // Dark overlay with hole
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawIntoCanvas { canvas ->
                val scrimColor = Color.Black.copy(alpha = 0.72f)
                canvas.drawRect(
                    left = 0f, top = 0f, right = size.width, bottom = size.height,
                    paint = Paint().apply { color = scrimColor }
                )

                if (highlightRect != Rect.Zero) {
                    val pad = 12f
                    val paint = Paint().apply {
                        blendMode = BlendMode.Clear
                    }
                    canvas.drawRoundRect(
                        left = highlightRect.left - pad,
                        top = highlightRect.top - pad,
                        right = highlightRect.right + pad,
                        bottom = highlightRect.bottom + pad,
                        radiusX = 16f,
                        radiusY = 16f,
                        paint = paint
                    )
                }
            }

            // Glow ring around highlighted element
            if (highlightRect != Rect.Zero) {
                val pad = 12f
                val glowColor = Color(0xFFFFD700).copy(alpha = glowAlpha * 0.6f)
                drawRoundRect(
                    color = glowColor,
                    topLeft = androidx.compose.ui.geometry.Offset(
                        highlightRect.left - pad - 6f,
                        highlightRect.top - pad - 6f
                    ),
                    size = androidx.compose.ui.geometry.Size(
                        highlightRect.width + (pad + 6f) * 2,
                        highlightRect.height + (pad + 6f) * 2
                    ),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(20f),
                    style = androidx.compose.ui.graphics.drawscope.Stroke(width = 3f)
                )
            }
        }

        // Explanation card – positioned below or above the highlight
        val density = LocalDensity.current
        val cardTopBias = if (highlightRect != Rect.Zero && highlightRect.bottom > 0.6f * LocalDensity.current.run { 700.dp.toPx() }) {
            // place above
            with(density) { (highlightRect.top - 220.dp.toPx()).roundToInt() }
        } else {
            // place below
            with(density) { (highlightRect.bottom + 24.dp.toPx()).roundToInt() }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .offset { IntOffset(0, cardTopBias.coerceAtLeast(with(density) { 80.dp.roundToPx() })) }
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Step title
                Text(
                    text = stepTitle,
                    color = Color(0xFFFFD700),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.graphicsLayer {
                        shadowElevation = 24f
                    },
                    style = LocalTextStyle.current.copy(
                        shadow = androidx.compose.ui.graphics.Shadow(
                            color = Color.Black,
                            offset = androidx.compose.ui.geometry.Offset(2f, 2f),
                            blurRadius = 8f
                        )
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Explanation text
                Text(
                    text = stepExplanation,
                    color = Color.White,
                    fontSize = 15.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 8.dp),
                    style = LocalTextStyle.current.copy(
                        shadow = androidx.compose.ui.graphics.Shadow(
                            color = Color.Black,
                            offset = androidx.compose.ui.geometry.Offset(1f, 1f),
                            blurRadius = 12f
                        )
                    )
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Navigation buttons
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Previous
                    OutlinedButton(
                        onClick = {
                            currentStep = when (currentStep) {
                                DemoStep.SONGBOOKS -> DemoStep.SEARCH_BOX
                                DemoStep.SONG_ITEM -> DemoStep.SONGBOOKS
                                DemoStep.FAB_BUTTON -> DemoStep.SONG_ITEM
                                else -> currentStep
                            }
                        },
                        enabled = currentStep != DemoStep.SEARCH_BOX,
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color.White,
                            disabledContentColor = Color.White.copy(alpha = 0.3f)
                        ),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (currentStep != DemoStep.SEARCH_BOX) Color.White.copy(alpha = 0.7f)
                            else Color.White.copy(alpha = 0.2f)
                        )
                    ) {
                        Text("Previous", fontSize = 13.sp)
                    }

                    // Next
                    Button(
                        onClick = {
                            currentStep = when (currentStep) {
                                DemoStep.SEARCH_BOX -> DemoStep.SONGBOOKS
                                DemoStep.SONGBOOKS -> DemoStep.SONG_ITEM
                                DemoStep.SONG_ITEM -> DemoStep.FAB_BUTTON
                                DemoStep.FAB_BUTTON -> DemoStep.DONE
                                else -> DemoStep.DONE
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFFFD700),
                            contentColor = Color.Black
                        )
                    ) {
                        Text(
                            if (currentStep == DemoStep.FAB_BUTTON) "Finish" else "Next",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Close
                    OutlinedButton(
                        onClick = { onDismiss() },
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color(0xFFFF6B6B)
                        ),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp, Color(0xFFFF6B6B).copy(alpha = 0.7f)
                        )
                    ) {
                        Text("Close", fontSize = 13.sp)
                    }
                }
            }
        }
    }
}
