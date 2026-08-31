package com.songlib.core.ui.components.share

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.layout.layout
import androidx.compose.ui.unit.Constraints

/**
 * Measures, places, and draws the content normally — so it can still be captured by a
 * `GraphicsLayer` — but reports zero size to its parent and is fully transparent, so it
 * never affects layout or shows up on screen. Used to render share-card composables
 * purely for image capture without a visible preview.
 */
fun Modifier.captureOnly(): Modifier = this
    .alpha(0f)
    .layout { measurable, _ ->
        val placeable = measurable.measure(Constraints())
        layout(0, 0) {
            placeable.place(0, 0)
        }
    }
