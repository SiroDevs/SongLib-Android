package com.songlib.presentation.splash.view

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
import com.songlib.R
import com.songlib.domain.repository.PrefsRepo
import com.songlib.presentation.navigation.Routes
import com.songlib.presentation.splash.components.*
import com.songlib.presentation.splash.SplashViewModel
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    navController: NavHostController,
    viewModel: SplashViewModel,
) {
    val context = LocalContext.current
    val prefs = remember { PrefsRepo(context) }
    val isLoading by viewModel.isLoading.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.initializeApp(context)
    }

    LaunchedEffect(Unit) {
        delay(3000)

        val nextRoute = when {
            prefs.selectAfresh -> Routes.STEP_1
            prefs.isDataLoaded -> Routes.HOME
            prefs.isDataSelected -> Routes.STEP_2
            else -> Routes.STEP_1
        }

        if (!isLoading) {
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
        },
    )
}