package com.songlib.presentation.screens.home.components

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController
import com.songlib.presentation.navigation.Routes
import com.songlib.presentation.viewmodels.HomeViewModel
import com.songlib.data.models.Listing
import com.songlib.presentation.components.listitems.ListingItem

@Composable
fun ListingsList(
    listings: List<Listing>,
    viewModel: HomeViewModel,
    navController: NavHostController,
    selectedListings: Set<Listing>,
    onListingSelected: (Listing) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
    ) {
        itemsIndexed(listings) { index, listing ->
            val isSelected = selectedListings.contains(listing)
            Box(
                modifier = Modifier
                    .combinedClickable(
                        onClick = {
                            if (selectedListings.isNotEmpty()) {
                                onListingSelected(listing)
                            } else {
                                navController.currentBackStackEntry
                                    ?.savedStateHandle
                                    ?.set("listing", listing)
                                navController.navigate(Routes.LISTING)
                            }
                        },
                        onLongClick = {
                            onListingSelected(listing)
                        }
                    )
                    .background(
                        if (isSelected) MaterialTheme.colorScheme.onPrimary
                        else Color.Transparent
                    )
            ) {
                ListingItem(listing = listing)
            }
        }
    }
}
