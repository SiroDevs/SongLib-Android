package com.songlib.presentation.splash.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp


@Composable
fun WithLoveFromRow() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            text = "with ",
            style = TextStyle(
                fontSize = 18.sp,
                letterSpacing = 5.sp,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        )
        Icon(
            imageVector = Icons.Filled.Favorite,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary
        )
        Text(
            text = " from",
            style = TextStyle(
                fontSize = 18.sp,
                letterSpacing = 5.sp,
                color = MaterialTheme.colorScheme.onPrimaryContainer
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
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        )
        Text(
            text = " & ",
            style = TextStyle(
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        )
        Text(
            text = "Titus",
            style = TextStyle(
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        )
    }
}