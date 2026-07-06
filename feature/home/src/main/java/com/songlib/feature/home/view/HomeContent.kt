package com.songlib.feature.home.view

import android.content.Intent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
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
import com.songlib.core.data.repos.PreferencesRepo
import com.songlib.core.ui.components.general.QuickFormDialog
import com.songlib.feature.home.HomeViewModel
import com.songlib.feature.home.components.ChoosingListingSheet
import com.songlib.feature.home.components.HomeAppBar
import com.songlib.feature.home.components.HomeTab
import com.songlib.feature.home.components.homeTabs
import com.songlib.feature.home.view.tabs.HomeLikes
import com.songlib.feature.home.view.tabs.HomeListings
import com.songlib.feature.home.view.tabs.HomeSearch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeContent(
    navController: NavHostController,
    viewModel: HomeViewModel,
    prefsRepo: PreferencesRepo,
) {
    val selectedTab by viewModel.selectedTab.collectAsState()
    val pagerState = rememberPagerState(
        initialPage = homeTabs.indexOf(selectedTab).coerceAtLeast(0),
        pageCount = { homeTabs.size }
    )

    val selectedSongs by viewModel.selectedSongs.collectAsState()
    val selectedListings by viewModel.selectedListings.collectAsState()
    val listings by viewModel.listings.collectAsState(initial = emptyList())
    val context = LocalContext.current

    var showListingSheet by remember { mutableStateOf(false) }
    var showAddListingDialog by remember { mutableStateOf(false) }

    LaunchedEffect(pagerState.currentPage) {
        viewModel.setSelectedTab(homeTabs[pagerState.currentPage])
    }

    LaunchedEffect(selectedTab) {
        val idx = homeTabs.indexOf(selectedTab)
        if (idx >= 0 && pagerState.currentPage != idx) {
            pagerState.animateScrollToPage(idx)
        }
    }

    if (showAddListingDialog) {
        QuickFormDialog(
            title = "New Listing",
            label = "Listing title",
            onDismiss = { showAddListingDialog = false },
            onConfirm = { title ->
                if (viewModel.checkAndHandleNewListing()) viewModel.saveListing(title)
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

    val topBarTitle = when {
        selectedSongs.isNotEmpty() -> "${selectedSongs.size} selected"
        selectedListings.isNotEmpty() -> "${selectedListings.size} selected"
        selectedTab == HomeTab.Search -> "SongLib"
        selectedTab == HomeTab.Likes -> "Liked Songs"
        selectedTab == HomeTab.Listings -> "Song Listings"
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
                onLikeSongs = { viewModel.likeSongs(selectedSongs) },
                onShareSong = {
                    val song = selectedSongs.first()
                    val shareText = songShareString(song.title, lyricsString(song.content))
                    val intent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_TEXT, shareText)
                    }
                    context.startActivity(Intent.createChooser(intent, "Share song via"))
                    viewModel.clearSongSelection()
                },
                onShowListingSheet = { showListingSheet = true },
                onDeleteListings = { viewModel.deleteListings(selectedListings) },
                onAddListing = { showAddListingDialog = true },
                viewModel = viewModel,
                navController = navController,
                prefsRepo = prefsRepo,
            )
        },
        bottomBar = {
            NavigationBar(containerColor = MaterialTheme.colorScheme.onPrimary) {
                homeTabs.forEach { homeTab ->
                    BottomNavigationItem(
                        icon = { Icon(homeTab.icon, contentDescription = homeTab.title) },
                        label = { Text(text = homeTab.title) },
                        selectedContentColor = MaterialTheme.colorScheme.primary,
                        unselectedContentColor = MaterialTheme.colorScheme.scrim,
                        alwaysShowLabel = true,
                        selected = selectedTab == homeTab,
                        onClick = { viewModel.setSelectedTab(homeTab) },
                    )
                }
            }
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
            when (homeTabs[page]) {
                HomeTab.Search -> HomeSearch(
                    viewModel = viewModel,
                    navController = navController,
                    prefsRepo = prefsRepo,
                    onShowDonation = { navController.navigate(Routes.DONATION) },
                )

                HomeTab.Likes -> HomeLikes(
                    viewModel = viewModel,
                    navController = navController,
                    prefsRepo = prefsRepo,
                    onShowDonation = { navController.navigate(Routes.DONATION) },
                )

                HomeTab.Listings -> HomeListings(
                    viewModel = viewModel,
                    navController = navController,
                    prefsRepo = prefsRepo,
                    onShowDonation = { navController.navigate(Routes.DONATION) },
                )
            }
        }
    }
}
