package com.songlib.feature.presenter.view

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.songlib.core.database.model.SongEntity
import com.songlib.core.ui.sample.*
import com.songlib.core.common.entity.UiState
import com.songlib.core.common.utils.lyricsString
import com.songlib.core.common.utils.songShareString
import com.songlib.core.data.repos.ThemeRepo
import com.songlib.core.designsystem.theme.ThemeSelectorDialog
import com.songlib.core.ui.components.action.AppTopBar
import com.songlib.core.ui.components.indicators.*
import com.songlib.feature.presenter.PresenterViewModel
import com.songlib.feature.presenter.components.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PresenterScreen(
    navController: NavHostController,
    viewModel: PresenterViewModel,
    song: SongEntity?,
    themeRepo: ThemeRepo
) {
    val horizontalSlides = viewModel.horizontalSlides
    val uiState by viewModel.uiState.collectAsState()
    val isLiked by viewModel.isLiked.collectAsState()
    val title by viewModel.title.collectAsState()
    val verses by viewModel.verses.collectAsState()
    val indicators by viewModel.indicators.collectAsState()
    val context = LocalContext.current

    var showMoreMenu by remember { mutableStateOf(false) }
    var showThemeDialog by remember { mutableStateOf(false) }
    val theme = themeRepo.selectedTheme

    LaunchedEffect(song) {
        song?.let { viewModel.loadSong(it) }
    }

    if (showThemeDialog) {
        ThemeSelectorDialog(
            current = theme,
            onDismiss = { showThemeDialog = false },
            onThemeSelected = {
                themeRepo.setTheme(it)
                showThemeDialog = false
            }
        )
    }

    Scaffold(
        topBar = {
            AppTopBar(
                title = title,
                titleMaxLines = 2,
                actions = {
                    LikeSongButton(
                        isLiked = isLiked,
                        song = song,
                        onLikeToggle = { viewModel.likeSong(it) }
                    )
                    // More menu
                    IconButton(onClick = { showMoreMenu = true }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "More")
                    }
                    DropdownMenu(
                        expanded = showMoreMenu,
                        onDismissRequest = { showMoreMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("App Theme") },
                            leadingIcon = { Icon(Icons.Default.Brightness6, contentDescription = null) },
                            onClick = {
                                showMoreMenu = false
                                showThemeDialog = true
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Edit Song (WIP)") },
                            leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null) },
                            enabled = false,
                            onClick = {
                                showMoreMenu = false
                                Toast.makeText(context, "Edit Song coming soon!", Toast.LENGTH_SHORT).show()
                            }
                        )
                    }
                },
                showGoBack = true,
                onNavIconClick = { navController.popBackStack() },
            )
        },
        floatingActionButton = {
            if (song != null) {
                FloatingActionButton(
                    onClick = {
                        val shareText = songShareString(song.title, lyricsString(song.content))
                        val intent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_TEXT, shareText)
                        }
                        context.startActivity(Intent.createChooser(intent, "Share song via"))
                    },
                    containerColor = MaterialTheme.colorScheme.onPrimary
                ) {
                    Icon(Icons.Default.Share, contentDescription = "Share song")
                }
            }
        },
        content = {
            Box(
                modifier = Modifier
                    .padding(it)
                    .fillMaxSize()
            ) {
                when (uiState) {
                    is UiState.Error -> ErrorState(
                        message = (uiState as UiState.Error).message,
                        retryAction = { }
                    )

                    UiState.Loaded -> PresenterContent(
                        verses = verses, indicators = indicators,
                        horizontalSlides = horizontalSlides
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
private fun LikeSongButton(
    isLiked: Boolean,
    song: SongEntity?,
    onLikeToggle: (SongEntity) -> Unit
) {
    val context = LocalContext.current

    IconButton(onClick = {
        song?.let {
            onLikeToggle(it)
            val message = if (isLiked) {
                "${it.title} removed from your likes"
            } else {
                "${it.title} added to your likes"
            }
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }) {
        Icon(
            imageVector = if (isLiked) Icons.Filled.Favorite else Icons.Default.FavoriteBorder,
            contentDescription = "Like Song",
            tint = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}

@Composable
fun PresenterContent(
    verses: List<String>, indicators: List<String>,
    horizontalSlides: Boolean = false,
) {
    val pagerState = rememberPagerState { verses.size }

    Column(
        modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.SpaceBetween
    ) {
        PresenterTabs(
            pagerState = pagerState, verses = verses, modifier = Modifier.weight(1f),
            horizontalSlides = horizontalSlides,
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
        verses = SampleVerses, indicators = SampleIndicators,
    )
}
