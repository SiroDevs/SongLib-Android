package com.songlib.core.ui.components.pagecurl

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.PaintingStyle
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.sin

/**
 * Which corner the curl originates from.
 */
enum class CurlCorner {
    TopLeft, TopRight, BottomLeft, BottomRight
}

fun DrawScope.drawPageCurl(
    corner: CurlCorner,
    progress: Float,                        // [0f, 1f]
    pageColor: Color = Color(0xFFEEEEEE),
) {
    if (progress <= 0f) return

    val w = size.width
    val h = size.height

    val maxReach = hypot(w * 0.45f, h * 0.45f) * progress

    val anchor = when (corner) {
        CurlCorner.TopLeft     -> Offset(0f, 0f)
        CurlCorner.TopRight    -> Offset(w, 0f)
        CurlCorner.BottomLeft  -> Offset(0f, h)
        CurlCorner.BottomRight -> Offset(w, h)
    }

    val foldAngle = when (corner) {
        CurlCorner.TopLeft     ->  PI / 4          // 45°  → toward bottom-right
        CurlCorner.TopRight    ->  3 * PI / 4      // 135° → toward bottom-left
        CurlCorner.BottomLeft  -> -PI / 4          // -45° → toward top-right
        CurlCorner.BottomRight -> -3 * PI / 4      // -135°→ toward top-left
    }

    val perpAngle = foldAngle + PI / 2

    // Centre of the fold line = anchor + maxReach along foldAngle
    val foldCx = anchor.x + maxReach * cos(foldAngle).toFloat()
    val foldCy = anchor.y + maxReach * sin(foldAngle).toFloat()

    // Half-length of the fold line visible on the page (extends perpendicular
    // to foldAngle, clamped so it doesn't exceed the page).
    val foldHalfLen = maxReach * 0.9f

    val p1 = Offset(
        (foldCx + foldHalfLen * cos(perpAngle)).toFloat(),
        (foldCy + foldHalfLen * sin(perpAngle)).toFloat(),
    )
    val p2 = Offset(
        (foldCx - foldHalfLen * cos(perpAngle)).toFloat(),
        (foldCy - foldHalfLen * sin(perpAngle)).toFloat(),
    )

    // --- Folded triangle (the page back-face) ---
    // Vertices: anchor corner + the two fold-line endpoints.
    val foldedTriangle = Path().apply {
        moveTo(anchor.x, anchor.y)
        lineTo(p1.x, p1.y)
        lineTo(p2.x, p2.y)
        close()
    }

    // Draw the back-face fill (slightly off-white / surface colour).
    drawPath(
        path = foldedTriangle,
        color = pageColor,
    )

    // --- Drop shadow along the fold edge ---
    // Simulates the penumbra that CurlMesh renders via its shadow strip.
    drawIntoCanvas { canvas ->
        val shadowPaint = Paint().apply {
            style = PaintingStyle.Stroke
            strokeWidth = 18f * progress
            asFrameworkPaint().apply {
                isAntiAlias = true
                // Semi-transparent black; opacity scales with progress so the
                // shadow fades in as the curl grows.
                color = android.graphics.Color.argb(
                    (100 * progress).toInt(),
                    0, 0, 0
                )
                maskFilter = android.graphics.BlurMaskFilter(
                    24f * progress,
                    android.graphics.BlurMaskFilter.Blur.NORMAL
                )
            }
        }
        val shadowPath = android.graphics.Path().apply {
            moveTo(p1.x, p1.y)
            lineTo(p2.x, p2.y)
        }
        canvas.nativeCanvas.drawPath(shadowPath, shadowPaint.asFrameworkPaint())
    }

    // --- Self-shadow on the curl underside ---
    // A thin dark gradient triangle overlaid on the folded face, slightly
    // inset from the fold edge, to give the illusion of the page curving away.
    val selfShadowAlpha = (0.18f * progress).coerceAtMost(0.18f)
    val innerP1 = Offset(
        anchor.x + (p1.x - anchor.x) * 0.1f,
        anchor.y + (p1.y - anchor.y) * 0.1f,
    )
    val innerP2 = Offset(
        anchor.x + (p2.x - anchor.x) * 0.1f,
        anchor.y + (p2.y - anchor.y) * 0.1f,
    )
    val selfShadowPath = Path().apply {
        moveTo(anchor.x, anchor.y)
        lineTo(p1.x, p1.y)
        lineTo(innerP1.x, innerP1.y)
        close()
        moveTo(anchor.x, anchor.y)
        lineTo(p2.x, p2.y)
        lineTo(innerP2.x, innerP2.y)
        close()
    }
    drawPath(
        path = selfShadowPath,
        color = Color.Black.copy(alpha = selfShadowAlpha),
    )

    drawLine(
        color = Color.White.copy(alpha = 0.35f * progress),
        start = p1,
        end = p2,
        strokeWidth = 2f,
    )
}