package com.songlib.core.ui.components.pagecurl

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@Composable
fun CornerNavZone(
    corner: CurlCorner,
    onTap: () -> Unit,
    modifier: Modifier = Modifier,
    image: (@Composable () -> Unit)? = null,
) {
    val scope = rememberCoroutineScope()
    val curlProgress = remember { Animatable(0f) }

    Box(
        modifier = modifier
            .drawWithContent {
                drawContent()
                drawPageCurl(
                    corner = corner,
                    progress = curlProgress.value,
                    pageColor = Color(0xFFEEEEEE),
                )
            }
    ) {
        Box(
            modifier =
                Modifier
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                    ) {
                        scope.launch {
                            curlProgress.animateTo(
                                targetValue = 1f,
                                animationSpec = tween(
                                    durationMillis = 220,
                                    easing = FastOutSlowInEasing
                                ),
                            )
                            onTap()
                            curlProgress.animateTo(
                                targetValue = 0f,
                                animationSpec = tween(
                                    durationMillis = 160,
                                    easing = FastOutSlowInEasing
                                ),
                            )
                        }
                    }
                    .padding(horizontal = 1.dp, vertical = 1.dp),
            contentAlignment = Alignment.Center,
        ) {
            image?.invoke()
        }
    }
}
