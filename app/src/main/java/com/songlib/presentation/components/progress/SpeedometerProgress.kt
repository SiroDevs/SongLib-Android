package com.songlib.presentation.components.progress

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp

@Composable
fun SpeedometerProgress(
    progress: Int,
    radius: Float,
    title: String
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        AdvancedProgress(
            radius = radius,
            levelAmount = 100,
            levelLowHeight = 16f,
            levelHighHeight = 20f,
            division = 10,
            secondaryWidth = 10f,
            progressGap = 10f,
            primaryValue = progress / 100f,
            secondaryValue = progress / 100f,
            primaryColor = Color.Yellow,
            secondaryColor = Color.Red,
            tertiaryColor = Color.White.copy(alpha = 0.24f)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "$progress %",
                    textAlign = TextAlign.Center,
                    fontSize = (radius / 1.5f).sp,
                    fontWeight = FontWeight.W400,
                    letterSpacing = 1.5.sp,
                    color = Color.White
                )
                Text(
                    text = title.uppercase(),
                    textAlign = TextAlign.Center,
                    fontSize = (radius / 8f).sp,
                    fontWeight = FontWeight.W800,
                    letterSpacing = 1.5.sp,
                    color = Color.White.copy(alpha = 0.24f)
                )
            }
        }
    }
}
//
//@Preview(showBackground = true)
//@Composable
//fun PreviewPresenterContent() {
//    SpeedometerProgress(
//        progress = 20,
//        radius = 200f,
//        title = "loading it now",
//    )
//}