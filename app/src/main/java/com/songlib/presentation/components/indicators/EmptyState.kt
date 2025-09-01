package com.songlib.presentation.components.indicators

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.songlib.R

@Composable
fun EmptyState(
    title: String = "Oops! It's empty here",
    message: String? = null,
    messageIcon: ImageVector? = null,
    actionTitle: String? = "Retry",
    onAction: (() -> Unit)? = null,
    titleColor: Color = MaterialTheme.colorScheme.primary,
    messageColor: Color = MaterialTheme.colorScheme.secondary,
    spacing: Dp = 20.dp
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.empty),
            contentDescription = null,
            modifier = Modifier
                .size(180.dp)
                .padding(bottom = 10.dp),
            contentScale = ContentScale.Fit
        )

        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            color = titleColor
        )

        message?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.titleMedium,
                color = messageColor,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(horizontal = 16.dp)
            )
        }

        messageIcon?.let { icon ->
            Icon(
                imageVector = icon,
                contentDescription = "Message icon",
//                tint = Color.Red,
                modifier = Modifier
                    .size(20.dp)
                    .padding(vertical = 10.dp)
            )
        }

        onAction?.let {
            Button(
                onClick = it,
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .width(200.dp)
            ) {
                Text(
                    text = actionTitle ?: "Retry",
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = Color.White
                    ),
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EmptyStatePreview() {
    MaterialTheme {
        EmptyState(
            message = "No songs were found.",
            onAction = { println("Retry tapped") }
        )
    }
}
