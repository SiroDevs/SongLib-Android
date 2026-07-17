package com.songlib.feature.home.view.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.FormatListNumbered
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.graphics.vector.ImageVector

sealed class HomeTab(var icon: ImageVector, var title: String) {
    object Search : HomeTab(Icons.Default.Search, "Search")
    object Likes : HomeTab(Icons.Default.FavoriteBorder, "Likes")
    object Listings : HomeTab(Icons.Default.FormatListNumbered, "Listings")
}

val homeTabs = listOf(
    HomeTab.Search,
    HomeTab.Likes,
    HomeTab.Listings
)