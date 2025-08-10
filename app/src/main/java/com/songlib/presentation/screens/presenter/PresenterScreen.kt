package com.songlib.presentation.screens.presenter

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.songlib.data.models.Song
import com.songlib.data.sample.*
import com.songlib.domain.entity.UiState
import com.songlib.presentation.components.action.AppTopBar
import com.songlib.presentation.components.indicators.LoadingState
import com.songlib.presentation.screens.presenter.components.*
import com.songlib.presentation.viewmodels.PresenterViewModel
import com.swahilib.presentation.components.indicators.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PresenterScreen(
    viewModel: PresenterViewModel,
    navController: NavHostController,
    song: Song?,
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    val isLiked by viewModel.isLiked.collectAsState()
    val title by viewModel.title.collectAsState()
    val verses by viewModel.verses.collectAsState()
    val indicators by viewModel.indicators.collectAsState()

    LaunchedEffect(song) {
        song?.let { viewModel.loadSong(it) }
    }

    Scaffold(topBar = {
        Surface(shadowElevation = 3.dp) {
            AppTopBar(
                title = title,
                actions = {
                    IconButton(onClick = {
                        song?.let {
                            viewModel.likeSong(it)

                            val text = if (isLiked) {
                                "${song.title} added to your likes"
                            } else {
                                "${song.title} removed from your likes"
                            }
                            Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
                        }
                    }) {
                        Icon(
                            imageVector = if (isLiked) Icons.Filled.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Like Song",
                        )
                    }
                },
                showGoBack = true,
                onNavIconClick = { navController.popBackStack() },
            )
        }
    }, content = {
        Box(
            modifier = Modifier
                .padding(it)
                .fillMaxSize()
        ) {
            when (uiState) {
                is UiState.Error -> ErrorState(
                    message = (uiState as UiState.Error).message,
                    onRetry = { }
                )

                UiState.Loaded -> PresenterContent(
                    verses = verses, indicators = indicators
                )

                UiState.Loading -> LoadingState(
                    title = "Loading song ...",
                    fileName = "circle-loader"
                )

                else -> EmptyState()
            }
        }
    })
}

@Composable
fun PresenterContent(
    verses: List<String>, indicators: List<String>
) {
    val pagerState = rememberPagerState { verses.size }

    Column(
        modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.SpaceBetween
    ) {
        PresenterTabs(
            pagerState = pagerState, verses = verses, modifier = Modifier.weight(1f)
        )

        PresenterIndicators(
            pagerState = pagerState,
            indicators = indicators,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 10.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewPresenterContent() {
    PresenterContent(
        verses = SampleVerses, indicators = SampleIndicators
    )
}
