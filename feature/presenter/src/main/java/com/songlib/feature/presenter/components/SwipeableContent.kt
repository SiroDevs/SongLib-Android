package com.songlib.feature.presenter.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.songlib.feature.presenter.view.PresenterContent

@Composable
fun SwipeableContent(
    verses: List<String>,
    indicators: List<String>,
    horizontalSlides: Boolean,
    onNavigateNext: () -> Unit,
    onNavigatePrevious: () -> Unit,
    hasPrevious: Boolean,
    hasNext: Boolean,
) {
    var isLongPressing by remember { mutableStateOf(false) }
    var dragOffsetX by remember { mutableStateOf(0f) }
    var isFlipping by remember { mutableStateOf(false) }
    var flipDirection by remember { mutableStateOf(0) } // -1 next, +1 prev

    // Page-flip rotation animation
    val flipRotation by animateFloatAsState(
        targetValue = if (isFlipping) flipDirection * 90f else 0f,
        animationSpec = tween(durationMillis = 250, easing = FastOutSlowInEasing),
        finishedListener = {
            if (isFlipping) {
                if (flipDirection == -1) onNavigateNext()
                else onNavigatePrevious()
                isFlipping = false
                flipDirection = 0
                dragOffsetX = 0f
                isLongPressing = false
            }
        },
        label = "flipRotation"
    )

    val dragThreshold = 80f

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(hasPrevious, hasNext) {
                detectHorizontalDragGestures(
                    onDragStart = { /* only active during long press — handled separately */ },
                    onDragEnd = {
                        if (isLongPressing && !isFlipping) {
                            when {
                                dragOffsetX < -dragThreshold && hasNext -> {
                                    flipDirection = -1
                                    isFlipping = true
                                }
                                dragOffsetX > dragThreshold && hasPrevious -> {
                                    flipDirection = 1
                                    isFlipping = true
                                }
                                else -> {
                                    dragOffsetX = 0f
                                    isLongPressing = false
                                }
                            }
                        }
                    },
                    onDragCancel = {
                        dragOffsetX = 0f
                        isLongPressing = false
                    },
                    onHorizontalDrag = { _, dragAmount ->
                        if (isLongPressing) {
                            dragOffsetX += dragAmount
                        }
                    }
                )
            }
            .pointerInput(Unit) {
                // Detect long press to activate song navigation mode
                awaitPointerEventScope {
                    while (true) {
                        val down = awaitPointerEvent()
                        val press = down.changes.firstOrNull() ?: continue
                        if (press.pressed) {
                            val startTime = System.currentTimeMillis()
                            var released = false
                            while (!released) {
                                val event = awaitPointerEvent()
                                val change = event.changes.firstOrNull()
                                if (change == null || !change.pressed) {
                                    released = true
                                } else if (System.currentTimeMillis() - startTime > 400L) {
                                    isLongPressing = true
                                }
                            }
                        }
                    }
                }
            }
    ) {
        // The song content with page-flip perspective transform
        Box(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    rotationY = flipRotation
                    cameraDistance = 12f * density
                }
        ) {
            PresenterContent(
                verses = verses,
                indicators = indicators,
                horizontalSlides = horizontalSlides,
            )
        }

        if (isLongPressing && !isFlipping) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.Center),
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    color = MaterialTheme.colorScheme.inverseSurface.copy(alpha = 0.85f),
                    shape = MaterialTheme.shapes.medium,
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (hasPrevious) {
                            Icon(
                                Icons.Default.ArrowBack,
                                contentDescription = "Previous song",
                                tint = MaterialTheme.colorScheme.inverseOnSurface
                            )
                            Text(
                                "Prev song",
                                color = MaterialTheme.colorScheme.inverseOnSurface,
                                style = MaterialTheme.typography.labelMedium
                            )
                        }
                        if (hasPrevious && hasNext) {
                            Text(
                                "  |  ",
                                color = MaterialTheme.colorScheme.inverseOnSurface.copy(alpha = 0.5f),
                                style = MaterialTheme.typography.labelMedium
                            )
                        }
                        if (hasNext) {
                            Text(
                                "Next song",
                                color = MaterialTheme.colorScheme.inverseOnSurface,
                                style = MaterialTheme.typography.labelMedium
                            )
                            Icon(
                                Icons.Default.ArrowForward,
                                contentDescription = "Next song",
                                tint = MaterialTheme.colorScheme.inverseOnSurface
                            )
                        }
                    }
                }
            }
        }
    }
}
