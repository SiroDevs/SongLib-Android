package com.songlib.core.ui.components.general
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.songlib.core.ui.R

@Composable
fun SplashContent() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.onPrimary)
            .statusBarsPadding()
            .navigationBarsPadding()
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
                modifier = Modifier.size(180.dp)
            )
            Spacer(Modifier.height(10.dp))
            Text(
                text = "SongLib",
                style = TextStyle(
                    fontSize = 35.sp,
                    letterSpacing = 5.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            )
            Spacer(Modifier.weight(1f))
            HorizontalDivider(
                modifier = Modifier
                    .padding(horizontal = 10.dp)
                    .height(1.dp),
                thickness = 2.dp,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Spacer(Modifier.height(5.dp))
            WithLoveFromRow()
            AppDevelopersRow()
            Spacer(Modifier.height(20.dp))
        }
    }
}

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