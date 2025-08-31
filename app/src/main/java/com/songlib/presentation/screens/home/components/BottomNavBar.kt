package com.songlib.presentation.screens.home.components

//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun BottomNavBar(
    selectedItem: HomeNavItem,
    onItemSelected: (HomeNavItem) -> Unit
) {
    val items = listOf(
        HomeNavItem.Search,
        HomeNavItem.Likes,
        HomeNavItem.Listings,
    )
    BottomNavigation(
        backgroundColor = MaterialTheme.colorScheme.onPrimary,
        contentColor = Color.White
    ) {
        items.forEach { item ->
            BottomNavigationItem(
                icon = { Icon(item.icon, contentDescription = item.title) },
                label = { Text(text = item.title) },
                selectedContentColor = MaterialTheme.colorScheme.primary,
                unselectedContentColor = MaterialTheme.colorScheme.scrim,
                alwaysShowLabel = true,
                selected = selectedItem == item,
                onClick = { onItemSelected(item) }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BottomNavBarPreview() {
    BottomNavBar(
        selectedItem = HomeNavItem.Search,
        onItemSelected = {}
    )
}

sealed class HomeNavItem(var icon: ImageVector, var title: String) {
    object Search : HomeNavItem(Icons.Default.Search, "Search")
    object Likes : HomeNavItem(Icons.Default.FavoriteBorder, "Likes")
    object Listings : HomeNavItem(Icons.Default.FormatListNumbered, "Listings")
}