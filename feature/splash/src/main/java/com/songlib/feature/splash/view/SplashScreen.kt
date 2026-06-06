package com.songlib.feature.splash.view

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import androidx.navigation.NavHostController
import com.songlib.feature.splash.R
import com.songlib.core.common.utils.Routes
import com.songlib.feature.splash.components.*
import com.songlib.feature.splash.SplashViewModel
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    navController: NavHostController,
    viewModel: SplashViewModel,
) {
    val context = LocalContext.current
    val isReady by viewModel.isReady.collectAsState()
    val destination by viewModel.destination.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.initializeApp(context)
    }

    LaunchedEffect(isReady) {
        if (isReady) {
            delay(3000)

            val nextRoute = when (destination) {
                is SplashViewModel.Destination.Selection -> Routes.SELECTION
                is SplashViewModel.Destination.Home -> Routes.HOME
            }

            navController.navigate(nextRoute) {
                popUpTo(Routes.SPLASH) { inclusive = true }
            }
        }
    }

    SplashContent()
}

@Preview(showBackground = true)
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