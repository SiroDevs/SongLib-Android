package com.songlib.feature.presenter.view

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Brightness6
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
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
import com.songlib.core.ui.components.indicators.EmptyState
import com.songlib.core.ui.components.indicators.ErrorState
import com.songlib.core.ui.components.indicators.LoadingState
import com.songlib.feature.presenter.PresenterViewModel
import com.songlib.feature.presenter.components.LikeSongButton
import com.songlib.feature.presenter.components.PresenterDemoOverlay
import com.songlib.feature.presenter.components.SwipeableContent

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

                    UiState.Loaded -> SwipeableContent(
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
