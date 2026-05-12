package com.songlib.feature.home.components

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.songlib.core.common.utils.Routes
import com.songlib.core.database.model.ListingUi
import com.songlib.core.ui.components.listitems.ListingItem

@Composable
fun ListingsList(
    listings: List<ListingUi>,
    navController: NavHostController,
    selectedListings: Set<ListingUi>,
    onListingSelected: (ListingUi) -> Unit
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
            Divider(
                color = MaterialTheme.colorScheme.surfaceVariant,
                thickness = 1.dp,
            )
        }
    }
}
