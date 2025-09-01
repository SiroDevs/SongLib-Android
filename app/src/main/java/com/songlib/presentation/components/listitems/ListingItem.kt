package com.songlib.presentation.components.listitems

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import com.songlib.data.models.Listing
import com.songlib.data.models.ListingUi

@Composable
fun ListingItem(listing: ListingUi) {
    Box(
        modifier = Modifier.padding(horizontal = 10.dp)
    )
    {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 5.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = listing.title,
                    maxLines = 1,
                    modifier = Modifier.weight(1f),
                    color = MaterialTheme.colorScheme.scrim,
                    style = TextStyle(
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
            }

            Spacer(modifier = Modifier.height(3.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "${listing.songCount} songs",
                    style = TextStyle(fontSize = 10.sp),
                    maxLines = 2,
                    modifier = Modifier.weight(1f),
                    color = MaterialTheme.colorScheme.scrim,
                )
                Spacer(modifier = Modifier.fillMaxWidth())
                Text(
                    text = listing.updatedAgo,
                    style = TextStyle(fontSize = 10.sp),
                    maxLines = 2,
                    modifier = Modifier.weight(1f),
                    color = MaterialTheme.colorScheme.scrim,
                )
            }
        }
    }
}
