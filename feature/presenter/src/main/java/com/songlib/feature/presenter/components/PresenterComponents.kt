package com.songlib.feature.presenter.components

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Brightness6
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.songlib.core.common.utils.lyricsString
import com.songlib.core.common.utils.songShareString
import com.songlib.core.database.model.SongEntity

@Composable
fun PresenterMoreMenu(
    expanded: Boolean,
    onDismiss: () -> Unit,
    onAddToList: () -> Unit,
    onAppTheme: () -> Unit,
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismiss,
    ) {
        DropdownMenuItem(
            text = { Text("Add to a List") },
            leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null) },
            onClick = onAddToList,
        )
        DropdownMenuItem(
            text = { Text("Edit Song (WIP)") },
            leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null) },
            enabled = false,
            onClick = {},
        )
        DropdownMenuItem(
            text = { Text("App Theme") },
            leadingIcon = { Icon(Icons.Default.Brightness6, contentDescription = null) },
            onClick = onAppTheme,
        )
    }
}

@Composable
fun PresenterFabColumn(
    fontSize: Float,
    currentSong: SongEntity?,
    onResetFontSize: () -> Unit,
    onShare: (String) -> Unit,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalAlignment = Alignment.End,
    ) {
        AnimatedVisibility(
            visible = fontSize != 28f,
            enter = slideInVertically { it } + fadeIn(),
            exit = slideOutVertically { it } + fadeOut(),
        ) {
            SmallFloatingActionButton(
                onClick = onResetFontSize,
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
            ) {
                Icon(Icons.Default.FormatSize, contentDescription = "Reset font size")
            }
        }

        if (currentSong != null) {
            FloatingActionButton(
                onClick = {
                    val shareText = songShareString(currentSong.title, lyricsString(currentSong.content))
                    onShare(shareText)
                },
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            ) {
                Icon(Icons.Default.Share, contentDescription = "Share song")
            }
        }
    }
}

@Composable
fun LikeSongButton(
    isLiked: Boolean,
    song: SongEntity?,
    onLikeToggle: (SongEntity) -> Unit
) {
    val context = LocalContext.current

    IconButton(onClick = {
        song?.let {
            onLikeToggle(it)
            val message = if (isLiked) {
                "${it.title} removed from your likes"
            } else {
                "${it.title} added to your likes"
            }
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }) {
        Icon(
            imageVector = if (isLiked) Icons.Filled.Favorite else Icons.Default.FavoriteBorder,
            contentDescription = "Like Song",
            tint = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}