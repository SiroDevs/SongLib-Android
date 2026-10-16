package com.songlib.feature.song.presentor.view.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.calculateZoom
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChanged
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.songlib.core.common.utils.AppFonts
import com.songlib.core.ui.components.pagecurl.CornerNavZone
import com.songlib.core.ui.components.pagecurl.CurlCorner
import com.songlib.feature.song.R
import com.songlib.feature.song.presentor.view.components.PresenterLayers
import kotlinx.coroutines.flow.SharedFlow

@Composable
fun PresenterContent(
    verses: List<String>,
    indicators: List<String>,
    songTitle: String,
    bookName: String?,
    horizontalSlides: Boolean,
    hasPrevious: Boolean,
    hasNext: Boolean,
    fontSize: Float,
    onFontSizeChange: (Float) -> Unit,
    onNavigatePrevious: () -> Unit,
    onNavigateNext: () -> Unit,
    onVerseIndexChanged: (Int) -> Unit = {},
    autoAdvanceTo: SharedFlow<Int>? = null,
    isAutoPlaying: Boolean = false,
    autoPlayFeatureEnabled: Boolean = true,
    autoPlayIsMonitoring: Boolean = true,
    autoPlayElapsedSeconds: Int = 0,
    autoPlayTotalSeconds: Int = 0,
    onToggleAutoPlay: () -> Unit = {},
) {
    var fontSizeAtGestureStart by remember { mutableFloatStateOf(fontSize) }
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
                        if (event.changes.size >= 2) {
                            if (!gestureStarted) {
                                fontSizeAtGestureStart = fontSize
                                gestureStarted = true
                            }
                            zoom *= event.calculateZoom()
                            val newSize = (fontSizeAtGestureStart * zoom).coerceIn(
                                AppFonts.MIN_FONT_SP,
                                AppFonts.MAX_FONT_SP,
                            )
                            onFontSizeChange(newSize)
                            event.changes.forEach { if (it.positionChanged()) it.consume() }
                        }
                    } while (event.changes.any { it.pressed })
                }
            }
    ) {
        PresenterLayers(
            verses = verses,
            indicators = indicators,
            songTitle = songTitle,
            bookName = bookName,
            horizontalSlides = horizontalSlides,
            fontSize = fontSize,
            onVerseIndexChanged = onVerseIndexChanged,
            autoAdvanceTo = autoAdvanceTo,
            isAutoPlaying = isAutoPlaying,
            autoPlayFeatureEnabled = autoPlayFeatureEnabled,
            autoPlayIsMonitoring = autoPlayIsMonitoring,
            autoPlayElapsedSeconds = autoPlayElapsedSeconds,
            autoPlayTotalSeconds = autoPlayTotalSeconds,
            onToggleAutoPlay = onToggleAutoPlay,
            cornerOverlay = {
                if (hasPrevious) {
                    CornerNavZone(
                        corner = CurlCorner.BottomLeft,
                        onTap = onNavigatePrevious,
                        modifier = Modifier
                            .align(Alignment.BottomStart),
                        image = {
                            Image(
                                painter = painterResource(id = R.drawable.curl_left),
                                contentDescription = "Previous",
                                modifier = Modifier.size(50.dp),
                            )
                        }
                    )
                }
                if (hasNext) {
                    CornerNavZone(
                        corner = CurlCorner.BottomRight,
                        onTap = onNavigateNext,
                        modifier = Modifier
                            .align(Alignment.BottomEnd),
                        image = {
                            Image(
                                painter = painterResource(id = R.drawable.curl_right),
                                contentDescription = "Next",
                                modifier = Modifier.size(50.dp),
                            )
                        }
                    )
                }
            }
        )
    }
}
