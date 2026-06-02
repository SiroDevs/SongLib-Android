package com.songlib.feature.presenter.view

import android.content.Intent
import android.widget.Toast
import androidx.compose.animation.core.*
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.songlib.core.common.entity.UiState
import com.songlib.core.common.utils.lyricsString
import com.songlib.core.common.utils.songShareString
import com.songlib.core.data.repos.PrefsRepo
import com.songlib.core.data.repos.ThemeRepo
import com.songlib.core.database.model.SongEntity
import com.songlib.core.designsystem.theme.ThemeSelectorDialog
import com.songlib.core.ui.components.action.AppTopBar
import com.songlib.core.ui.components.indicators.*
import com.songlib.core.ui.sample.*
import com.songlib.feature.presenter.PresenterViewModel
import com.songlib.feature.presenter.components.*
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PresenterScreen(
    navController: NavHostController,
    viewModel: PresenterViewModel,
    song: SongEntity?,
    themeRepo: ThemeRepo,
    prefsRepo: PrefsRepo,
) {
    val horizontalSlides = viewModel.horizontalSlides
    val uiState by viewModel.uiState.collectAsState()
    val isLiked by viewModel.isLiked.collectAsState()
    val title by viewModel.title.collectAsState()
    val verses by viewModel.verses.collectAsState()
    val indicators by viewModel.indicators.collectAsState()
    val currentSong by viewModel.currentSong.collectAsState()
    val context = LocalContext.current

    var showMoreMenu by remember { mutableStateOf(false) }
    var showThemeDialog by remember { mutableStateOf(false) }
    val theme = themeRepo.selectedTheme

    var showPresenterDemo by remember { mutableStateOf(viewModel.demoMode) }

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
//                titleMaxLines = 2,
                actions = {
                    LikeSongButton(
                        isLiked = isLiked,
                        song = currentSong,
                        onLikeToggle = { viewModel.likeSong(it) }
                    )
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
            val activeSong = currentSong
            if (activeSong != null) {
                FloatingActionButton(
                    onClick = {
                        val shareText = songShareString(activeSong.title, lyricsString(activeSong.content))
                        val intent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_TEXT, shareText)
                        }
                        context.startActivity(Intent.createChooser(intent, "Share song via"))
                    },
                    containerColor = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.padding(bottom = 100.dp)
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

                    UiState.Loaded -> SwipeablePresenterContent(
                        verses = verses,
                        indicators = indicators,
                        horizontalSlides = horizontalSlides,
                        onNavigateNext = { viewModel.navigateToNext() },
                        onNavigatePrevious = { viewModel.navigateToPrevious() },
                        hasPrevious = viewModel.hasPreviousSong,
                        hasNext = viewModel.hasNextSong,
                    )

                    UiState.Loading -> LoadingState(
                        title = "Loading song ...",
                        fileName = "circle-loader"
                    )

                    else -> EmptyState()
                }

                PresenterDemoOverlay(
                    isVisible = showPresenterDemo,
                    onDismiss = { showPresenterDemo = false }
                )
            }
        })
}

/**
 * Wraps [PresenterContent] with long-press + swipe detection for song-to-song navigation.
 * A page-flip animation is triggered in the direction of the swipe.
 */
@Composable
private fun SwipeablePresenterContent(
    verses: List<String>,
    indicators: List<String>,
    horizontalSlides: Boolean,
    onNavigateNext: () -> Unit,
    onNavigatePrevious: () -> Unit,
    hasPrevious: Boolean,
    hasNext: Boolean,
) {
    var isLongPressing by remember { mutableStateOf(false) }
    var dragOffsetX by remember { mutableStateOf(0f) }
    var isFlipping by remember { mutableStateOf(false) }
    var flipDirection by remember { mutableStateOf(0) } // -1 next, +1 prev

    // Page-flip rotation animation
    val flipRotation by animateFloatAsState(
        targetValue = if (isFlipping) flipDirection * 90f else 0f,
        animationSpec = tween(durationMillis = 250, easing = FastOutSlowInEasing),
        finishedListener = {
            if (isFlipping) {
                if (flipDirection == -1) onNavigateNext()
                else onNavigatePrevious()
                isFlipping = false
                flipDirection = 0
                dragOffsetX = 0f
                isLongPressing = false
            }
        },
        label = "flipRotation"
    )

    val dragThreshold = 80f

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(hasPrevious, hasNext) {
                detectHorizontalDragGestures(
                    onDragStart = { /* only active during long press — handled separately */ },
                    onDragEnd = {
                        if (isLongPressing && !isFlipping) {
                            when {
                                dragOffsetX < -dragThreshold && hasNext -> {
                                    flipDirection = -1
                                    isFlipping = true
                                }
                                dragOffsetX > dragThreshold && hasPrevious -> {
                                    flipDirection = 1
                                    isFlipping = true
                                }
                                else -> {
                                    dragOffsetX = 0f
                                    isLongPressing = false
                                }
                            }
                        }
                    },
                    onDragCancel = {
                        dragOffsetX = 0f
                        isLongPressing = false
                    },
                    onHorizontalDrag = { _, dragAmount ->
                        if (isLongPressing) {
                            dragOffsetX += dragAmount
                        }
                    }
                )
            }
            .pointerInput(Unit) {
                // Detect long press to activate song navigation mode
                awaitPointerEventScope {
                    while (true) {
                        val down = awaitPointerEvent()
                        val press = down.changes.firstOrNull() ?: continue
                        if (press.pressed) {
                            val startTime = System.currentTimeMillis()
                            var released = false
                            while (!released) {
                                val event = awaitPointerEvent()
                                val change = event.changes.firstOrNull()
                                if (change == null || !change.pressed) {
                                    released = true
                                } else if (System.currentTimeMillis() - startTime > 400L) {
                                    isLongPressing = true
                                }
                            }
                        }
                    }
                }
            }
    ) {
        // The song content with page-flip perspective transform
        Box(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    rotationY = flipRotation
                    cameraDistance = 12f * density
                }
        ) {
            PresenterContent(
                verses = verses,
                indicators = indicators,
                horizontalSlides = horizontalSlides,
            )
        }

        // Long-press hint indicator
        if (isLongPressing && !isFlipping) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.Center),
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    color = MaterialTheme.colorScheme.inverseSurface.copy(alpha = 0.85f),
                    shape = MaterialTheme.shapes.medium,
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (hasPrevious) {
                            Icon(
                                Icons.Default.ArrowBack,
                                contentDescription = "Previous song",
                                tint = MaterialTheme.colorScheme.inverseOnSurface
                            )
                            Text(
                                "Prev song",
                                color = MaterialTheme.colorScheme.inverseOnSurface,
                                style = MaterialTheme.typography.labelMedium
                            )
                        }
                        if (hasPrevious && hasNext) {
                            Text(
                                "  |  ",
                                color = MaterialTheme.colorScheme.inverseOnSurface.copy(alpha = 0.5f),
                                style = MaterialTheme.typography.labelMedium
                            )
                        }
                        if (hasNext) {
                            Text(
                                "Next song",
                                color = MaterialTheme.colorScheme.inverseOnSurface,
                                style = MaterialTheme.typography.labelMedium
                            )
                            Icon(
                                Icons.Default.ArrowForward,
                                contentDescription = "Next song",
                                tint = MaterialTheme.colorScheme.inverseOnSurface
                            )
                        }
                    }
                }
            }
        }
    }
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
    verses: List<String>,
    indicators: List<String>,
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
