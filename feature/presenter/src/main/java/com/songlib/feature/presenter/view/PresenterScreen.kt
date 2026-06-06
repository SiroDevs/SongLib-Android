package com.songlib.feature.presenter.view

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import com.songlib.core.common.entity.UiState
import com.songlib.core.data.repos.PrefsRepo
import com.songlib.core.data.repos.ThemeRepo
import com.songlib.core.database.model.BookEntity
import com.songlib.core.database.model.SongEntity
import com.songlib.core.designsystem.theme.ThemeSelectorDialog
import com.songlib.core.ui.components.action.AppTopBar
import com.songlib.core.ui.components.general.QuickFormDialog
import com.songlib.core.ui.components.indicators.EmptyState
import com.songlib.core.ui.components.indicators.ErrorState
import com.songlib.core.ui.components.indicators.LoadingState
import com.songlib.feature.home.components.ChoosingListingSheet
import com.songlib.feature.presenter.PresenterViewModel
import com.songlib.feature.presenter.components.LikeSongButton
import com.songlib.feature.presenter.components.PresenterDemoOverlay
import com.songlib.feature.presenter.components.PresenterFabColumn
import com.songlib.feature.presenter.components.PresenterMoreMenu
import com.songlib.feature.presenter.components.SwipeableContent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PresenterScreen(
    navController: NavHostController,
    viewModel: PresenterViewModel,
    song: SongEntity?,
    book: BookEntity?,
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
    val listings by viewModel.listings.collectAsState()
    val fontSize by viewModel.fontSize.collectAsState()
    val context = LocalContext.current

    val theme = themeRepo.selectedTheme
    var showMoreMenu by remember { mutableStateOf(false) }
    var showThemeDialog by remember { mutableStateOf(false) }
    var showListingSheet by remember { mutableStateOf(false) }
    var showAddListingDialog by remember { mutableStateOf(false) }
    var showPresenterDemo by rememberSaveable { mutableStateOf(viewModel.demoMode) }

    LaunchedEffect(Unit) {
        viewModel.toastEvent.collect { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

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

    if (showAddListingDialog) {
        QuickFormDialog(
            title = "New Listing",
            label = "Listing title",
            onDismiss = { showAddListingDialog = false },
            onConfirm = { newTitle ->
                viewModel.saveListing(newTitle)
                showAddListingDialog = false
            }
        )
    }

    if (showListingSheet) {
        ChoosingListingSheet(
            listings = listings,
            onDismiss = { showListingSheet = false },
            onNewListClick = {
                showListingSheet = false
                showAddListingDialog = true
            },
            onListingClick = { listing ->
                viewModel.saveListItem(listing, currentSong?.songId ?: song?.songId ?: 0)
                showListingSheet = false
            },
            onDone = { showListingSheet = false }
        )
    }

    Scaffold(
        topBar = {
            AppTopBar(
                title = title,
                tagline = book?.title,
                showGoBack = true,
                onNavIconClick = { navController.popBackStack() },
                actions = {
                    LikeSongButton(
                        isLiked = isLiked,
                        song = currentSong,
                        onLikeToggle = { viewModel.likeSong(it) }
                    )
                    IconButton(onClick = { showMoreMenu = true }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "More")
                    }
                    PresenterMoreMenu(
                        expanded = showMoreMenu,
                        onDismiss = { showMoreMenu = false },
                        onAddToList = {
                            showMoreMenu = false
                            if (viewModel.checkAndHandleNewListing()) showListingSheet = true
                            else showAddListingDialog = true
                        },
                        onAppTheme = {
                            showMoreMenu = false
                            showThemeDialog = true
                        },
                    )
                },
            )
        },
        floatingActionButton = {
            PresenterFabColumn(
                fontSize = fontSize,
                currentSong = currentSong,
                onResetFontSize = { viewModel.updateFontSize(PresenterViewModel.DEFAULT_FONT_SP) },
                onShare = { shareText ->
                    val intent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_TEXT, shareText)
                    }
                    context.startActivity(Intent.createChooser(intent, "Share song via"))
                },
            )
        },
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            when (uiState) {
                is UiState.Error -> ErrorState(
                    message = (uiState as UiState.Error).message,
                    retryAction = {}
                )

                UiState.Loaded -> SwipeableContent(
                    verses = verses,
                    indicators = indicators,
                    horizontalSlides = horizontalSlides,
                    onNavigateNext = { viewModel.navigateToNext() },
                    onNavigatePrevious = { viewModel.navigateToPrevious() },
                    hasPrevious = viewModel.hasPreviousSong,
                    hasNext = viewModel.hasNextSong,
                    fontSize = fontSize,
                    onFontSizeChange = { viewModel.updateFontSize(it) },
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
    }
}
