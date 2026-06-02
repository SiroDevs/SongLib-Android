package com.songlib.feature.home.view

import android.content.Intent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import com.songlib.core.common.utils.Routes
import com.songlib.core.common.utils.lyricsString
import com.songlib.core.common.utils.songShareString
import com.songlib.core.data.repos.PrefsRepo
import com.songlib.core.data.repos.ThemeRepo
import com.songlib.core.designsystem.theme.ThemeSelectorDialog
import com.songlib.core.ui.components.general.QuickFormDialog
import com.songlib.feature.home.HomeViewModel
import com.songlib.feature.home.components.BottomNavBar
import com.songlib.feature.home.components.HomeAppBar
import com.songlib.feature.home.components.HomeNavItem
import com.songlib.feature.home.view.tabs.HomeLikes
import com.songlib.feature.home.view.tabs.HomeListings
import com.songlib.feature.home.view.tabs.HomeSearch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeContent(
    viewModel: HomeViewModel,
    navController: NavHostController,
    themeRepo: ThemeRepo,
    prefsRepo: PrefsRepo,
) {
    val tabs = listOf(HomeNavItem.Search, HomeNavItem.Likes, HomeNavItem.Listings)
    val selectedTab by viewModel.selectedTab.collectAsState()
    var showThemeDialog by remember { mutableStateOf(false) }
    var showMoreMenu by remember { mutableStateOf(false) }
    var showDonationDialog by remember { mutableStateOf(false) }
    val theme = themeRepo.selectedTheme
    val pagerState = rememberPagerState(
        initialPage = tabs.indexOf(selectedTab).coerceAtLeast(0),
        pageCount = { tabs.size }
    )

    val selectedSongs by viewModel.selectedSongs.collectAsState()
    val selectedListings by viewModel.selectedListings.collectAsState()
    val listings by viewModel.listings.collectAsState(initial = emptyList())
    val context = LocalContext.current

    var showListingSheet by remember { mutableStateOf(false) }
    var showAddListingDialog by remember { mutableStateOf(false) }

    LaunchedEffect(pagerState.currentPage) {
        viewModel.setSelectedTab(tabs[pagerState.currentPage])
    }

    LaunchedEffect(selectedTab) {
        val idx = tabs.indexOf(selectedTab)
        if (idx >= 0 && pagerState.currentPage != idx) {
            pagerState.animateScrollToPage(idx)
        }
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
            onConfirm = { title ->
                if (viewModel.checkAndHandleNewListing()) {
                    viewModel.saveListing(title)
                }
                showAddListingDialog = false
            }
        )
    }

    if (showListingSheet) {
        com.songlib.feature.home.components.ChoosingListingSheet(
            listings = listings,
            onDismiss = { showListingSheet = false },
            onNewListClick = {
                showListingSheet = false
                if (viewModel.checkAndHandleNewListing()) showAddListingDialog = true
            },
            onListingClick = { listing ->
                viewModel.saveListItems(listing, selectedSongs)
                showListingSheet = false
                viewModel.clearSongSelection()
            },
            onDone = { showListingSheet = false }
        )
    }

    val hasSelection = selectedSongs.isNotEmpty() || selectedListings.isNotEmpty()

    val topBarTitle = when {
        selectedSongs.isNotEmpty() -> "${selectedSongs.size} selected"
        selectedListings.isNotEmpty() -> "${selectedListings.size} selected"
        selectedTab == HomeNavItem.Search -> "SongLib"
        selectedTab == HomeNavItem.Likes -> "Liked Songs"
        selectedTab == HomeNavItem.Listings -> "Song Listings"
        else -> "SongLib"
    }

    Scaffold(
        topBar = {
            HomeAppBar(
                title = topBarTitle,
                selectedTab = selectedTab,
                selectedSongs = selectedSongs,
                selectedListings = selectedListings,
                onClearSongSelection = viewModel::clearSongSelection,
                onClearListingSelection = viewModel::clearListingSelection,
                onLikeSongs = {
                    viewModel.likeSongs(selectedSongs)
                },
                onShareSong = {
                    val song = selectedSongs.first()

                    val shareText = songShareString(
                        song.title,
                        lyricsString(song.content)
                    )

                    val intent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_TEXT, shareText)
                    }

                    context.startActivity(
                        Intent.createChooser(intent, "Share song via")
                    )

                    viewModel.clearSongSelection()
                },
                onShowListingSheet = {
                    showListingSheet = true
                },
                onDeleteListings = {
                    viewModel.deleteListings(selectedListings)
                },
                onShowThemeDialog = {
                    showThemeDialog = true
                },
                onAddListing = {
                    showAddListingDialog = true
                },
                onNavigateSettings = {
                    navController.navigate(Routes.SETTINGS)
                },
                onNavigateHowItWorks = {
                    navController.navigate(Routes.HOW_IT_WORKS)
                },
                onNavigateHelp = {
                    navController.navigate(Routes.HELP)
                }
            )
        },
        bottomBar = {
            BottomNavBar(
                selectedItem = selectedTab,
                onItemSelected = { viewModel.setSelectedTab(it) }
            )
        }
    ) { paddingValues ->
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = paddingValues.calculateBottomPadding())
                .padding(top = paddingValues.calculateTopPadding()),
            userScrollEnabled = true,
        ) { page ->
            when (tabs[page]) {
                HomeNavItem.Search -> HomeSearch(
                    viewModel = viewModel,
                    navController = navController,
                    prefsRepo = prefsRepo,
                    onShowDonationDialog = { showDonationDialog = true },
                )
                HomeNavItem.Likes -> HomeLikes(
                    viewModel = viewModel,
                    navController = navController,
                )
                HomeNavItem.Listings -> HomeListings(
                    viewModel = viewModel,
                    navController = navController,
                )
            }
        }
    }
}
