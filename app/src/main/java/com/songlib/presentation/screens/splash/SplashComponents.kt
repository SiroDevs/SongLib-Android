package com.songlib.presentation.screens.splash

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.songlib.presentation.theme.*


@Composable
fun WithLoveFromRow() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            text = "with ",
            style = TextStyle(
                fontSize = 30.sp,
                letterSpacing = 5.sp,
                fontWeight = FontWeight.Bold,
                color = ThemeColors.primary1
            )
        )
        Icon(
            imageVector = Icons.Filled.Favorite,
            contentDescription = null,
            tint = ThemeColors.primaryDark1
        )
        Text(
            text = " from",
            style = TextStyle(
                fontSize = 30.sp,
                letterSpacing = 5.sp,
                fontWeight = FontWeight.Bold,
                color = ThemeColors.primary1
            )
        )
    }
}

@Composable
fun AppDevelopersRow() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Siro",
            style = TextStyle(
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = ThemeColors.primaryDark1
            )
        )
        Text(
            text = " & ",
            style = TextStyle(
                fontSize = 20.sp,
                color = ThemeColors.primary1
            )
        )
        Text(
            text = "Titus",
            style = TextStyle(
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = ThemeColors.primaryDark1
            )
        )
    }
}