package com.songlib.feature.home.view

import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EditNote
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
import com.songlib.core.common.entity.UiState
import com.songlib.core.common.utils.Routes
import com.songlib.core.data.repos.ThemeRepo
import com.songlib.core.designsystem.theme.ThemeSelectorDialog
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
    val theme = themeRepo.selectedTheme
    val pagerState = rememberPagerState(
        initialPage = tabs.indexOf(selectedTab).coerceAtLeast(0),
        pageCount = { tabs.size }
    )

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

    Scaffold(
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
                .padding(bottom = paddingValues.calculateBottomPadding()),
            userScrollEnabled = true,
        ) { page ->
            when (tabs[page]) {
                HomeNavItem.Search -> HomeSearch(
                    viewModel = viewModel,
                    navController = navController,
                    onShowThemeDialog = {
                        showThemeDialog = true
                    }
                )
                HomeNavItem.Likes -> HomeLikes(
                    viewModel = viewModel,
                    navController = navController,
                    onShowThemeDialog = {
                        showThemeDialog = true
                    }
                )
                HomeNavItem.Listings -> HomeListings(
                    viewModel = viewModel,
                    navController = navController,
                    onShowThemeDialog = {
                        showThemeDialog = true
                    }
                )
            }
        }
    }
}
