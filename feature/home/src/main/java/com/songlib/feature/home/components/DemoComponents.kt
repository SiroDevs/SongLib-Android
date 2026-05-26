package com.songlib.feature.home.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.collections.copy

@Composable
fun DemoStepLabel(text: String) {
    Text(
        text       = text,
        color      = Color(0xFFFFD700),
        fontSize   = 18.sp,
        fontWeight = FontWeight.Bold,
        textAlign  = TextAlign.Center,
        style      = LocalTextStyle.current.copy(
            shadow = Shadow(
                color      = Color.Black,
                offset     = Offset(2f, 2f),
                blurRadius = 8f,
            )
        ),
    )
}

@Composable
fun DemoStepExplanation(text: String) {
    Text(
        text      = text,
        color     = Color.White,
        fontSize  = 15.sp,
        textAlign = TextAlign.Center,
        modifier  = Modifier.padding(horizontal = 8.dp),
        style     = LocalTextStyle.current.copy(
            shadow = Shadow(
                color      = Color.Black,
                offset     = Offset(1f, 1f),
                blurRadius = 12f,
            )
        ),
    )
}

@Composable
fun DemoNavButtons(
    isFirst:    Boolean,
    isLast:     Boolean,
    onPrevious: () -> Unit,
    onNext:     () -> Unit,
    onClose:    () -> Unit,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally),
        modifier              = Modifier.fillMaxWidth(),
    ) {
        OutlinedButton(
            onClick  = onPrevious,
            enabled  = !isFirst,
            colors   = ButtonDefaults.outlinedButtonColors(
                contentColor         = Color.White,
                disabledContentColor = Color.White.copy(alpha = 0.3f),
            ),
            border = BorderStroke(
                1.dp,
                if (!isFirst) Color.White.copy(alpha = 0.7f) else Color.White.copy(alpha = 0.2f),
            ),
        ) {
            Text("Previous", fontSize = 13.sp)
        }

        Button(
            onClick = onNext,
            colors  = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFFFD700),
                contentColor   = Color.Black,
            ),
        ) {
            Text(
                text       = if (isLast) "Finish" else "Next",
                fontSize   = 13.sp,
                fontWeight = FontWeight.Bold,
            )
        }

        OutlinedButton(
            onClick = onClose,
            colors  = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFFF6B6B)),
            border  = BorderStroke(1.dp, Color(0xFFFF6B6B).copy(alpha = 0.7f)),
        ) {
            Text("Close", fontSize = 13.sp)
        }
    }
}