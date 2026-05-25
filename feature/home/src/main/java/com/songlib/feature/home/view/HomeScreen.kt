package com.songlib.feature.home.view

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import com.songlib.core.common.entity.UiState
import com.songlib.core.common.utils.Routes
import com.songlib.core.common.utils.lyricsString
import com.songlib.core.common.utils.songShareString
import com.songlib.core.data.repos.ThemeRepo
import com.songlib.core.database.model.SongEntity
import com.songlib.core.designsystem.theme.ThemeSelectorDialog
import com.songlib.core.ui.components.action.AppTopBar
import com.songlib.core.ui.components.indicators.EmptyState
import com.songlib.core.ui.components.indicators.ErrorState
import com.songlib.core.ui.components.indicators.LoadingState
import com.songlib.feature.home.HomeViewModel
import com.songlib.feature.home.components.BottomNavBar
import com.songlib.feature.home.components.HomeNavItem
import com.songlib.feature.home.view.tabs.HomeLikes
import com.songlib.feature.home.view.tabs.HomeListings
import com.songlib.feature.home.view.tabs.HomeSearch
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun HomeScreen(
    navController: NavHostController,
    viewModel: HomeViewModel,
    themeRepo: ThemeRepo
) {
    val uiState by viewModel.uiState.collectAsState()
    val songs by viewModel.songs.collectAsState(initial = emptyList())
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.toastEvent.collectLatest { msg ->
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(Unit) {
        viewModel.fetchData()
    }

    when (uiState) {
        is UiState.Error -> {
            ErrorState(
                message = (uiState as UiState.Error).message,
                retryAction = { viewModel.fetchData() }
            )
        }
        UiState.Loading -> {
            LoadingState(title = "", fileName = "circle-loader")
        }
        UiState.Filtered -> {
            if (songs.isEmpty()) {
                EmptyState(
                    message = "It appears you didn't finish your songbook selection, that's why it's empty here at the moment.\n\nLet's fix that asap!",
                    messageIcon = Icons.Default.EditNote,
                    onAction = {
                        viewModel.clearData { success ->
                            if (success) {
                                navController.navigate(Routes.SPLASH) {
                                    popUpTo(0) { inclusive = true }
                                    launchSingleTop = true
                                }
                            }
                        }
                    }
                )
            } else {
                MainHomeContent(
                    viewModel = viewModel,
                    navController = navController,
                    themeRepo = themeRepo
                )
            }
        }
        else -> EmptyState()
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun MainHomeContent(
    viewModel: HomeViewModel,
    navController: NavHostController,
    themeRepo: ThemeRepo
) {
    val tabs = listOf(HomeNavItem.Search, HomeNavItem.Likes, HomeNavItem.Listings)
    val selectedTab by viewModel.selectedTab.collectAsState()
    var showThemeDialog by remember { mutableStateOf(false) }
    var showMoreMenu by remember { mutableStateOf(false) }
    val theme = themeRepo.selectedTheme
    val pagerState = rememberPagerState(
        initialPage = tabs.indexOf(selectedTab).coerceAtLeast(0),
        pageCount = { tabs.size }
    )

    val selectedSongs by viewModel.selectedSongs.collectAsState()
    val selectedListings by viewModel.selectedListings.collectAsState()
    val listings by viewModel.listings.collectAsState(initial = emptyList())
    val context = LocalContext.current

    // Signals from child tabs -> lifted actions
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
        com.songlib.core.ui.components.general.QuickFormDialog(
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
            AppTopBar(
                title = topBarTitle,
                showGoBack = hasSelection,
                onNavIconClick = {
                    if (selectedSongs.isNotEmpty()) viewModel.clearSongSelection()
                    else viewModel.clearListingSelection()
                },
                actions = {
                    if (!hasSelection) {
                        if (selectedTab == HomeNavItem.Listings) {
                            IconButton(onClick = { showAddListingDialog = true }) {
                                Icon(Icons.Filled.Add, contentDescription = "New listing")
                            }
                        }
                        IconButton(onClick = { showThemeDialog = true }) {
                            Icon(Icons.Filled.Brightness6, contentDescription = "Theme")
                        }
                        IconButton(onClick = { showMoreMenu = true }) {
                            Icon(Icons.Default.MoreVert, contentDescription = "More")
                        }
                        DropdownMenu(
                            expanded = showMoreMenu,
                            onDismissRequest = { showMoreMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Settings") },
                                leadingIcon = { Icon(Icons.Default.Settings, contentDescription = null) },
                                onClick = {
                                    showMoreMenu = false
                                    navController.navigate(Routes.SETTINGS)
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("How It Works") },
                                leadingIcon = { Icon(Icons.Default.Info, contentDescription = null) },
                                onClick = {
                                    showMoreMenu = false
                                    navController.navigate(Routes.HOW_IT_WORKS)
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Help & Feedback") },
                                leadingIcon = { Icon(Icons.Default.HelpOutline, contentDescription = null) },
                                onClick = {
                                    showMoreMenu = false
                                    navController.navigate(Routes.HELP)
                                }
                            )
                        }
                    } else if (selectedSongs.isNotEmpty()) {
                        val allLiked = selectedSongs.all { it.liked }
                        IconButton(onClick = { viewModel.likeSongs(selectedSongs) }) {
                            Icon(
                                if (allLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = if (allLiked) "Unlike" else "Like",
                                tint = if (allLiked) MaterialTheme.colorScheme.primary
                                       else MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                        if (selectedSongs.size == 1) {
                            val song = selectedSongs.first()
                            IconButton(onClick = {
                                val shareText = songShareString(song.title, lyricsString(song.content))
                                val intent = Intent(Intent.ACTION_SEND).apply {
                                    type = "text/plain"
                                    putExtra(Intent.EXTRA_TEXT, shareText)
                                }
                                context.startActivity(Intent.createChooser(intent, "Share song via"))
                                viewModel.clearSongSelection()
                            }) {
                                Icon(Icons.Default.Share, contentDescription = "Share")
                            }
                        }
                        IconButton(onClick = { showListingSheet = true }) {
                            Icon(Icons.Default.FormatListNumbered, contentDescription = "Add to listing")
                        }
                    } else if (selectedListings.isNotEmpty()) {
                        IconButton(onClick = { viewModel.deleteListings(selectedListings) }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete")
                        }
                    }
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
