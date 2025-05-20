package com.songlib.presentation.screens.home.search

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.songlib.domain.entities.UiState
import com.songlib.presentation.components.EmptyState
import com.songlib.presentation.viewmodels.HomeViewModel

@Composable
fun SearchScreen(
    viewModel: HomeViewModel,
) {
    val uiState by viewModel.uiState.collectAsState()
    val filtered by viewModel.filtered.collectAsState(initial = emptyList())

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {

        when (uiState) {
            is UiState.Filtered ->
                EmptyState()
            /*LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(horizontal = 10.dp)
            ) {
                items(filtered) { song ->
                    Workout(
                        activity = song,
                        onActivityClick = { },
                    )
                }
            }*/

            else -> EmptyState()
        }
    }
}