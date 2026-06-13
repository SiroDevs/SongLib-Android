package com.songlib.feature.home.view

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.songlib.core.common.entity.UiState
import com.songlib.core.common.utils.Routes
import com.songlib.core.data.repos.PrefsRepo
import com.songlib.core.ui.components.indicators.EmptyState
import com.songlib.core.ui.components.indicators.ErrorState
import com.songlib.core.ui.components.listitems.SongSkeletonItem
import com.songlib.core.ui.components.listitems.shimmerBrush
import com.songlib.feature.home.HomeViewModel
import kotlinx.coroutines.flow.collectLatest

@Composable
fun HomeScreen(
    navController: NavHostController,
    viewModel: HomeViewModel,
    prefsRepo: PrefsRepo,
) {
    val uiState by viewModel.uiState.collectAsState()
    val songs   by viewModel.songs.collectAsState(initial = emptyList())
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.toastEvent.collectLatest { msg ->
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(Unit) { viewModel.fetchData() }

    when (uiState) {
        is UiState.Error -> ErrorState(
            message = (uiState as UiState.Error).message,
            retryAction = { viewModel.fetchData() }
        )

        UiState.Loading -> HomeSkeletonScreen()

        UiState.Filtered -> {
            if (songs.isEmpty()) {
                EmptyState(
                    message = "It appears you didn't finish your songbook selection, " +
                            "that's why it's empty here at the moment.\n\nLet's fix that asap!",
                    messageIcon = Icons.Default.EditNote,
                    onAction = {
                        viewModel.clearData { success ->
                            if (success) {
                                navController.navigate(Routes.APP_START) {
                                    popUpTo(0) { inclusive = true }
                                    launchSingleTop = true
                                }
                            }
                        }
                    }
                )
            } else {
                HomeContent(
                    viewModel = viewModel,
                    navController = navController,
                    prefsRepo = prefsRepo
                )
            }
        }

        else -> EmptyState()
    }
}

@Composable
private fun HomeSkeletonScreen() {
    val brush = shimmerBrush()
    Scaffold { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
        ) {
            // Search bar skeleton
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp, vertical = 10.dp)
                        .height(56.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(brush)
                )
            }
            item {
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    items(5) {
                        Box(
                            modifier = Modifier
                                .width(72.dp)
                                .height(28.dp)
                                .clip(RoundedCornerShape(20.dp))
                                .background(brush)
                        )
                    }
                }
            }
            items(12) { SongSkeletonItem() }
        }
    }
}
