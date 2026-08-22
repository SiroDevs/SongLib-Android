package com.songlib.feature.home.view.components

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Cast
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.FormatListNumbered
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.songlib.core.common.utils.Routes
import com.songlib.core.data.repos.PrefsRepo
import com.songlib.core.database.model.ListingUi
import com.songlib.core.database.model.SongEntity
import com.songlib.core.ui.components.action.AppTopBar
import coil.compose.AsyncImage

@Composable
fun HomeAppBar(
    title: String,
    selectedTab: HomeTab,
    selectedSongs: Set<SongEntity>,
    selectedListings: Set<ListingUi>,
    onClearSongSelection: () -> Unit,
    onClearListingSelection: () -> Unit,
    onLikeSongs: () -> Unit,
    onShareSong: () -> Unit,
    onShowListingSheet: () -> Unit,
    onDeleteListings: () -> Unit,
    onAddListing: () -> Unit,
    navController: NavHostController,
    prefsRepo: PrefsRepo,
) {
    var showMoreMenu by remember { mutableStateOf(false) }
    val hasSelection = selectedSongs.isNotEmpty() || selectedListings.isNotEmpty()

    AppTopBar(
        title = title,
        showGoBack = hasSelection,
        onNavIconClick = {
            if (selectedSongs.isNotEmpty()) onClearSongSelection()
            else onClearListingSelection()
        },
        actions = {
            when {
                !hasSelection -> {
                    if (selectedTab == HomeTab.Listings) {
                        IconButton(onClick = onAddListing) {
                            Icon(Icons.Default.Add, contentDescription = "New listing")
                        }
                    }

                    IconButton(onClick = { navController.navigate(Routes.CASTING) }) {
                        Icon(Icons.Default.Cast, contentDescription = "Casting to PC")
                    }

                    HomeUserProfile(
                        prefsRepo = prefsRepo,
                        navController = navController,
                    )

                    IconButton(onClick = { showMoreMenu = true }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "More")
                    }

                    HomeOverflowMenu(
                        expanded = showMoreMenu,
                        onDismiss = { showMoreMenu = false },
                        navController = navController,
                    )
                }

                selectedSongs.isNotEmpty() -> {
                    val allLiked = selectedSongs.all { it.liked }
                    IconButton(onClick = onLikeSongs) {
                        Icon(
                            imageVector = if (allLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = if (allLiked) "Unlike" else "Like",
                            tint = if (allLiked) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                    if (selectedSongs.size == 1) {
                        IconButton(onClick = onShareSong) {
                            Icon(Icons.Default.Share, contentDescription = "Share")
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
                        Icon(Icons.Default.Delete, contentDescription = "Delete")
                    }
                }
            }
        }
    )
}

@Composable
fun HomeUserProfile(
    prefsRepo: PrefsRepo,
    navController: NavHostController,
) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    var isLoggedIn by remember { mutableStateOf(prefsRepo.isLoggedIn) }
    var photoUrl by remember { mutableStateOf(prefsRepo.loggedInPhotoUrl) }

    LaunchedEffect(backStackEntry) {
        isLoggedIn = prefsRepo.isLoggedIn
        photoUrl = prefsRepo.loggedInPhotoUrl
    }

    IconButton(onClick = { navController.navigate(Routes.ACCOUNT) }) {
        if (isLoggedIn && photoUrl.isNotEmpty()) {
            AsyncImage(
                model = photoUrl,
                contentDescription = "Profile photo",
                modifier = Modifier
                    .size(25.dp)
                    .clip(CircleShape)
            )
        } else {
            Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = "Account",
                modifier = Modifier.size(25.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
