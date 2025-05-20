package com.songlib.presentation.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.songlib.R
import com.songlib.presentation.screens.home.likes.LikesScreen
import com.songlib.presentation.screens.home.search.SearchScreen
import com.songlib.presentation.screens.home.widgets.*
import com.songlib.presentation.viewmodels.HomeViewModel

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
) {
    val navController = rememberNavController()
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.app_name), fontSize = 18.sp) },
                backgroundColor = colorResource(id = R.color.purple_500),
                contentColor = Color.White
            )
        },
        content = { padding ->
            Box(modifier = Modifier.padding(padding)) {
                Navigation(navController = navController)
            }
        },
        bottomBar = { BottomNavigationBar(navController) },
    )
}

@Composable
fun Navigation(navController: NavHostController) {
    NavHost(navController, startDestination = NavigationItem.Search.route) {
        composable(NavigationItem.Search.route) {
            SearchScreen()
        }
        composable(NavigationItem.Likes.route) {
            LikesScreen()
        }
    }
}