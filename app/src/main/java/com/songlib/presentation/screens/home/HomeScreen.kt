package com.songlib.presentation.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.songlib.presentation.viewmodels.HomeViewModel
import com.songlib.presentation.viewmodels.SelectionViewModel

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    navController: NavHostController,
) {
    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Text(
            text = "Hello My Name",
            modifier = Modifier.padding(innerPadding)
        )
    }
}