package com.songlib.presentation.screens.splash

import android.content.Context
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import androidx.navigation.NavHostController
import com.songlib.R
import com.songlib.core.utils.PrefConstants
import com.songlib.presentation.navigation.Routes
import com.songlib.presentation.theme.*
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavHostController) {
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        delay(3000)

        val prefs =
            context.getSharedPreferences(PrefConstants.PREFERENCE_FILE, Context.MODE_PRIVATE)
        val isDataSelected = prefs.getBoolean(PrefConstants.DATA_SELECTED, false)
        val isDataLoaded = prefs.getBoolean(PrefConstants.DATA_LOADED, false)

        val nextRoute = when {
            isDataLoaded -> Routes.HOME
            isDataSelected -> Routes.STEP_2
            else -> Routes.STEP_1
        }

        navController.navigate(nextRoute) {
            popUpTo(Routes.SPLASH) { inclusive = true }
        }
    }

    SplashContent()
}

@Preview(showBackground = true)
@Composable
fun SplashContent() {
    Scaffold(
        content = {
            Box(
                modifier = Modifier
                    .padding(it)
                    .fillMaxSize()
                    .background(color = MaterialTheme.colorScheme.onPrimary)
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
                            color = MaterialTheme.colorScheme.primary
                        )
                    )
                    Spacer(Modifier.weight(1f))
                    HorizontalDivider(
                        modifier = Modifier
                            .padding(horizontal = 10.dp)
                            .height(1.dp),
                        thickness = 2.dp,
                        color = MaterialTheme.colorScheme.inversePrimary
                    )
                    WithLoveFromRow()
                    AppDevelopersRow()
                    Spacer(Modifier.height(20.dp))
                }
            }
        },
    )
}