package com.songlib.presentation.components.listitems

import android.graphics.fonts.FontStyle
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.*
import com.songlib.presentation.theme.ThemeColors

@Composable
fun TagItem(tagText: String, height: Dp) {
    if (tagText.isNotEmpty()) {
        Box(
            modifier = Modifier
                //.padding(end = 5)//Sizes.xs)
                .background(
                    color = if (isSystemInDarkTheme()) ThemeColors.primary2 else ThemeColors.primary,
                    shape = RoundedCornerShape(5.dp)
                )
                .padding(horizontal = 10.dp, vertical = 3.dp)
        ) {
            Text(
                text = tagText,
                /*style = TextStyles.headingStyle5.copy(
                    color = Color.White,
                    letterSpacing = 1.sp,
                    //fontStyle = FontStyle.Italic
                )*/
            )
        }
    }
}
