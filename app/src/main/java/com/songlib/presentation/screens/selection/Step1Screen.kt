package com.songlib.presentation.screens.selection

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.songlib.domain.entities.UiState
import com.songlib.presentation.viewmodels.SelectionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Step1Screen(
    viewModel: SelectionViewModel,
    navController: NavHostController,
) {
    var fetchData by rememberSaveable { mutableStateOf(0) }

    if (fetchData == 0) {
        viewModel.fetchBooks()
        fetchData = fetchData.inc()
    }

    val books by viewModel.books.collectAsState(initial = emptyList())
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            Surface(shadowElevation = 3.dp) {
                TopAppBar(
                    title = { Text("Select Songbooks") },
                    actions = {
                        IconButton(onClick = { }) {
                            Icon(
                                imageVector = Icons.Filled.Refresh,
                                contentDescription = "Refresh"
                            )
                        }
                    },
                )
            }
        },
        content = {
            Box(
                modifier = Modifier
                    .padding(it)
                    .fillMaxSize()
                    .background(color = MaterialTheme.colorScheme.surface)
            ) {

            }
        }
    )
}