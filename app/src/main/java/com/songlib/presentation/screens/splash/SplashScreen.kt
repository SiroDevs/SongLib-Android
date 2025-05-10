package com.songlib.presentation.screens.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import com.songlib.R
import com.songlib.presentation.theme.*

@Composable
fun SplashScreen() {
    Scaffold(
        content = {
            Box(
                modifier = Modifier
                    .padding(it)
                    .fillMaxSize()
                    .background(color = ThemeColors.white)
            ) {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Spacer(Modifier.weight(1f))
                    Image(
                        painter = painterResource(id = R.drawable.app_icon),
                        contentDescription = "App logo",
                        modifier = Modifier.size(200.dp)
                    )
                    Spacer(Modifier.height(10.dp))
                    Text(
                        text = "SongLib",
                        style = TextStyle(
                            fontSize = 50.sp,
                            letterSpacing = 5.sp,
                            fontWeight = FontWeight.Bold,
                            color = ThemeColors.primary
                        )
                    )
                    Spacer(Modifier.weight(1f))
                    Divider(
                        color = ThemeColors.primaryDark,
                        thickness = 2.dp,
                        modifier = Modifier
                            .padding(horizontal = 10.dp)
                            .height(1.dp)
                    )
                    WithLoveFromRow()
                    AppDevelopersRow()
                    Spacer(Modifier.height(20.dp))
                }
            }
        },
    )
}

@Preview
@Composable
fun SplashScreenPreview() {
    SplashScreen()
}