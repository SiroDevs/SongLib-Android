package com.songlib.feature.how_it_works.view

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FormatListNumbered
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Slideshow
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.songlib.core.ui.components.action.AppTopBar

data class HowItWorksSection(
    val icon: ImageVector,
    val title: String,
    val description: String
)

private val sections = listOf(
    HowItWorksSection(
        icon = Icons.Default.CheckCircle,
        title = "Selection",
        description = "When you first open SongLib, you'll be presented with a list of available songbooks. " +
                "Tap on any songbook to select or deselect it. You can choose one or more songbooks to include " +
                "in your library. Once you're happy with your selection, tap the confirm button to load the songs. " +
                "You can always modify your collection later from the Settings screen."
    ),
    HowItWorksSection(
        icon = Icons.Default.Search,
        title = "Searching",
        description = "The Search tab is your main way to find songs. Type any part of a song title or number " +
                "in the search bar to filter results instantly. For songs with numbers, you can also use the " +
                "dial pad (the floating button) to search by song number — just tap the digits to narrow down " +
                "your search. The results update in real time as you type."
    ),
    HowItWorksSection(
        icon = Icons.Default.Favorite,
        title = "Song Likes",
        description = "Found a song you love? You can like it while viewing it in the presenter by tapping the " +
                "heart icon in the top bar. You can also like songs from the search list — long press a song " +
                "to select it, then use the like button. All your liked songs are collected in the Likes tab " +
                "so you can access your favourites quickly."
    ),
    HowItWorksSection(
        icon = Icons.Default.FormatListNumbered,
        title = "Song Listings",
        description = "Listings let you group songs together into custom playlists — useful for worship sets, " +
                "events, or personal collections. Create a new listing from the Listings tab or while selecting " +
                "songs in Search. Tap a listing to view or manage its songs. You can have up to 3 listings at " +
                "a time. To add songs to a listing, select them in the Search tab and tap the listing icon."
    ),
    HowItWorksSection(
        icon = Icons.Default.Slideshow,
        title = "Song Presentation",
        description = "Tap any song to open it in the presenter. The song is displayed verse by verse for easy " +
                "reading. Swipe left or right to move between verses, or tap the dots at the bottom to jump " +
                "to a specific verse. You can switch between vertical and horizontal slide orientation in " +
                "Settings. Use the share button to share the song with others via any app on your phone."
    ),
)

@Composable
fun HowItWorksScreen(
    navController: NavHostController,
) {
    Scaffold(
        topBar = {
            AppTopBar(
                title = "How It Works",
                showGoBack = true,
                onNavIconClick = { navController.popBackStack() }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Learn how to get the most out of SongLib",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 4.dp)
            )

            sections.forEach { section ->
                HowItWorksCard(section = section)
            }

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
private fun HowItWorksCard(section: HowItWorksSection) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize()
            .clickable { expanded = !expanded },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.size(40.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = section.icon,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
                Text(
                    text = section.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = if (expanded) "Collapse" else "Expand",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (expanded) {
                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = section.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = MaterialTheme.typography.bodyMedium.lineHeight
                )
            }
        }
    }
}
