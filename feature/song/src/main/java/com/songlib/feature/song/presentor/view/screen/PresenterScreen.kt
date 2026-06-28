package com.songlib.feature.song.presentor.view.screen

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
import com.songlib.core.common.utils.AppFonts
import com.songlib.core.common.utils.Routes
import com.songlib.core.data.repos.PrefsRepo
import com.songlib.core.database.model.BookEntity
import com.songlib.core.database.model.SongEntity
import com.songlib.core.ui.components.action.AppTopBar
import com.songlib.core.ui.components.general.QuickFormDialog
import com.songlib.core.ui.components.indicators.EmptyState
import com.songlib.core.ui.components.indicators.ErrorState
import com.songlib.feature.home.components.ChoosingListingSheet
import com.songlib.feature.song.presentor.viewmodel.PresenterViewModel
import com.songlib.feature.song.presentor.viewmodel.ReportUiState
import com.songlib.feature.song.presentor.view.components.DemoOverlay
import com.songlib.feature.song.presentor.view.components.LikeSongBtn
import com.songlib.feature.song.presentor.view.components.PresentorMoreMenu
import com.songlib.feature.song.presentor.view.components.PresentorFab
import com.songlib.feature.song.presentor.view.components.ReportSongDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PresenterScreen(
    navController: NavHostController,
    viewModel: PresenterViewModel,
    song: SongEntity?,
    book: BookEntity?,
    prefsRepo: PrefsRepo,
) {
    val horizontalSlides = viewModel.horizontalSlides
    val uiState by viewModel.uiState.collectAsState()
    val isLiked by viewModel.isLiked.collectAsState()
    val hasPreviousSong by viewModel.hasPreviousSong.collectAsState()
    val hasNextSong by viewModel.hasNextSong.collectAsState()
    val title by viewModel.title.collectAsState()
    val verses by viewModel.verses.collectAsState()
    val indicators by viewModel.indicators.collectAsState()
    val currentSong by viewModel.currentSong.collectAsState()
    val listings by viewModel.listings.collectAsState()
    val fontSize by viewModel.fontSize.collectAsState()
    val reportState by viewModel.reportState.collectAsState()
    val context = LocalContext.current

    var showMoreMenu by remember { mutableStateOf(false) }
    var showReportDialog by remember { mutableStateOf(false) }
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

    LaunchedEffect(reportState) {
        if (reportState is ReportUiState.Success) {
            showReportDialog = false
            viewModel.resetReportState()
        }
    }

    if (showReportDialog) {
        ReportSongDialog(
            songTitle = currentSong?.title ?: song?.title ?: "",
            isSubmitting = reportState is ReportUiState.Submitting,
            onDismiss = {
                showReportDialog = false
                viewModel.resetReportState()
            },
            onSubmit = { type, desc ->
                val s = currentSong ?: song
                s?.let {
                    viewModel.submitReport(
                        song = it,
                        bookId = book?.bookId ?: it.book,
                        reportType = type,
                        description = desc
                    )
                }
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
                    LikeSongBtn(
                        isLiked = isLiked,
                        song = currentSong,
                        onLikeToggle = { viewModel.likeSong(it) }
                    )
                    IconButton(onClick = { showMoreMenu = true }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "More")
                    }
                    PresentorMoreMenu(
                        expanded = showMoreMenu,
                        onDismiss = { showMoreMenu = false },
                        onAddToList = {
                            if (viewModel.checkAndHandleNewListing()) showListingSheet = true
                            else showAddListingDialog = true
                        },
                        onReportSong = { showReportDialog = true },
                        onEditSong = {
                            // Navigate to the song editor
                            val songToEdit = currentSong ?: song
                            songToEdit?.let { s ->
                                navController.currentBackStackEntry
                                    ?.savedStateHandle
                                    ?.set("song_to_edit", s)
                                navController.navigate(Routes.EDITOR)
                            }
                        },
                        onCopyToDrafts = {
                            val songToCopy = currentSong ?: song
                            songToCopy?.let { viewModel.copyToDrafts(it) }
                        },
                    )
                },
            )
        },
        floatingActionButton = {
            PresentorFab(
                fontSize = fontSize,
                currentSong = currentSong,
                onResetFontSize = { viewModel.updateFontSize(AppFonts.DEFAULT_FONT_SP) },
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

                UiState.Loaded -> PresenterContent(
                    verses = verses,
                    indicators = indicators,
                    horizontalSlides = horizontalSlides,
                    hasPrevious = hasPreviousSong,
                    hasNext = hasNextSong,
                    fontSize = fontSize,
                    onFontSizeChange = { viewModel.updateFontSize(it) },
                    onNavigatePrevious = { viewModel.navigateToPrevious() },
                    onNavigateNext = { viewModel.navigateToNext() },
                    onVerseIndexChanged = { viewModel.onVerseIndexChanged(it) },
                )

                UiState.Loading -> { }

                else -> EmptyState()
            }

            DemoOverlay(
                isVisible = showPresenterDemo,
                onDismiss = { showPresenterDemo = false }
            )
        }
    }
}
