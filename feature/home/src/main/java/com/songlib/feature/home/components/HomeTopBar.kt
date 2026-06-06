package com.songlib.feature.home.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Brightness6
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.FormatListNumbered
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.songlib.core.data.repos.ThemeRepo
import com.songlib.core.database.model.ListingUi
import com.songlib.core.database.model.SongEntity
import com.songlib.core.designsystem.theme.ThemeSelectorDialog
import com.songlib.core.ui.components.action.AppTopBar
import kotlin.collections.isNotEmpty

@Composable
fun HomeAppBar(
    title: String,
    selectedTab: HomeNavItem,
    selectedSongs: Set<SongEntity>,
    selectedListings: Set<ListingUi>,
    onClearSongSelection: () -> Unit,
    onClearListingSelection: () -> Unit,
    onLikeSongs: () -> Unit,
    onShareSong: () -> Unit,
    onShowListingSheet: () -> Unit,
    onDeleteListings: () -> Unit,
    onAddListing: () -> Unit,
    onNavigateSettings: () -> Unit,
    onNavigateHowItWorks: () -> Unit,
    onNavigateHelp: () -> Unit,
    themeRepo: ThemeRepo,
) {
    var showMoreMenu by remember { mutableStateOf(false) }
    var showThemeDialog by remember { mutableStateOf(false) }
    val theme = themeRepo.selectedTheme
    val hasSelection = selectedSongs.isNotEmpty() || selectedListings.isNotEmpty()

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

    AppTopBar(
        title = title,
        showGoBack = hasSelection,
        onNavIconClick = {
            if (selectedSongs.isNotEmpty()) {
                onClearSongSelection()
            } else {
                onClearListingSelection()
            }
        },
        actions = {
            when {
                !hasSelection -> {
                    if (selectedTab == HomeNavItem.Listings) {
                        IconButton(onClick = onAddListing) {
                            Icon(
                                Icons.Default.Add,
                                contentDescription = "New listing"
                            )
                        }
                    }

                    IconButton(onClick = { showThemeDialog = true }) {
                        Icon(
                            Icons.Default.Brightness6,
                            contentDescription = "Theme"
                        )
                    }

                    IconButton(onClick = { showMoreMenu = true }) {
                        Icon(
                            Icons.Default.MoreVert,
                            contentDescription = "More"
                        )
                    }

                    DropdownMenu(
                        expanded = showMoreMenu,
                        onDismissRequest = { showMoreMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Settings") },
                            leadingIcon = {
                                Icon(
                                    Icons.Default.Settings,
                                    contentDescription = null
                                )
                            },
                            onClick = {
                                showMoreMenu = false
                                onNavigateSettings()
                            }
                        )

                        DropdownMenuItem(
                            text = { Text("How It Works") },
                            leadingIcon = {
                                Icon(
                                    Icons.Default.Info,
                                    contentDescription = null
                                )
                            },
                            onClick = {
                                showMoreMenu = false
                                onNavigateHowItWorks()
                            }
                        )

                        DropdownMenuItem(
                            text = { Text("Help & Feedback") },
                            leadingIcon = {
                                Icon(
                                    Icons.Default.HelpOutline,
                                    contentDescription = null
                                )
                            },
                            onClick = {
                                showMoreMenu = false
                                onNavigateHelp()
                            }
                        )
                    }
                }

                selectedSongs.isNotEmpty() -> {
                    val allLiked = selectedSongs.all { it.liked }

                    IconButton(onClick = onLikeSongs) {
                        Icon(
                            imageVector = if (allLiked) {
                                Icons.Default.Favorite
                            } else {
                                Icons.Default.FavoriteBorder
                            },
                            contentDescription = if (allLiked) "Unlike" else "Like",
                            tint = if (allLiked) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.onPrimaryContainer
                            }
                        )
                    }

                    if (selectedSongs.size == 1) {
                        IconButton(onClick = onShareSong) {
                            Icon(
                                Icons.Default.Share,
                                contentDescription = "Share"
                            )
                        }
                    }

                    IconButton(onClick = onShowListingSheet) {
                        Icon(
                            Icons.Default.FormatListNumbered,
                            contentDescription = "Add to listing"
                        )
                    }
                }

                selectedListings.isNotEmpty() -> {
                    IconButton(onClick = onDeleteListings) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Delete"
                        )
                    }
                }
            }
        }
    )
}
