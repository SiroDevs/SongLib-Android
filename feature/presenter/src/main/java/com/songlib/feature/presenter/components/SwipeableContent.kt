package com.songlib.feature.presenter.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.calculateZoom
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
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChanged
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
    fontSize: Float,
    onFontSizeChange: (Float) -> Unit,
) {
    var isLongPressing by remember { mutableStateOf(false) }
    var dragOffsetX by remember { mutableFloatStateOf(0f) }
    var isFlipping by remember { mutableStateOf(false) }
    var flipDirection by remember { mutableStateOf(0) }

    var fontSizeAtGestureStart by remember { mutableFloatStateOf(fontSize) }

    val flipRotation by animateFloatAsState(
        targetValue = if (isFlipping) flipDirection * 90f else 0f,
        animationSpec = tween(durationMillis = 250, easing = FastOutSlowInEasing),
        finishedListener = {
            if (isFlipping) {
                if (flipDirection == -1) onNavigateNext() else onNavigatePrevious()
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
            .pointerInput(Unit) {
                awaitEachGesture {
                    awaitFirstDown(requireUnconsumed = false)
                    var zoom = 1f
                    var gestureStarted = false

                    do {
                        val event = awaitPointerEvent()
                        val zoomChange = event.calculateZoom()

                        if (event.changes.size >= 2) {
                            if (!gestureStarted) {
                                fontSizeAtGestureStart = fontSize
                                gestureStarted = true
                            }
                            zoom *= zoomChange
                            onFontSizeChange(fontSizeAtGestureStart * zoom)

                            event.changes.forEach { change ->
                                if (change.positionChanged()) change.consume()
                            }
                        }
                    } while (event.changes.any { it.pressed })
                }
            }
            .pointerInput(hasPrevious, hasNext) {
                detectHorizontalDragGestures(
                    onDragStart = {},
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
                        if (isLongPressing) dragOffsetX += dragAmount
                    }
                )
            }
            .pointerInput(Unit) {
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
                fontSize = fontSize,
            )
        }

//        if (isLongPressing && !isFlipping) {
//            Box(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .align(Alignment.Center),
//                contentAlignment = Alignment.Center
//            ) {
//                Surface(
//                    color = MaterialTheme.colorScheme.inverseSurface.copy(alpha = 0.85f),
//                    shape = MaterialTheme.shapes.medium,
//                ) {
//                    Row(
//                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
//                        horizontalArrangement = Arrangement.spacedBy(8.dp),
//                        verticalAlignment = Alignment.CenterVertically
//                    ) {
//                        if (hasPrevious) {
//                            Icon(
//                                Icons.Default.ArrowForward,
//                                contentDescription = "Previous song",
//                                tint = MaterialTheme.colorScheme.inverseOnSurface
//                            )
//                            Text(
//                                "Swipe Right to the Previous song",
//                                color = MaterialTheme.colorScheme.inverseOnSurface,
//                                style = MaterialTheme.typography.labelMedium
//                            )
//                        }
//                        if (hasPrevious && hasNext) {
//                            Text(
//                                "  |  ",
//                                color = MaterialTheme.colorScheme.inverseOnSurface.copy(alpha = 0.5f),
//                                style = MaterialTheme.typography.labelMedium
//                            )
//                        }
//                        if (hasNext) {
//                            Text(
//                                "Swipe Left to the Next song",
//                                color = MaterialTheme.colorScheme.inverseOnSurface,
//                                style = MaterialTheme.typography.labelMedium
//                            )
//                            Icon(
//                                Icons.Default.ArrowBack,
//                                contentDescription = "Next song",
//                                tint = MaterialTheme.colorScheme.inverseOnSurface
//                            )
//                        }
//                    }
//                }
//            }
//        }
    }
}
