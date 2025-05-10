package com.songlib.presentation.screens.selection

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.songlib.presentation.viewmodels.SelectionViewModel

@Composable
fun Step2Screen(
    viewModel: SelectionViewModel,
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