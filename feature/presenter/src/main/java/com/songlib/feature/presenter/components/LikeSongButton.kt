package com.songlib.feature.presenter.components

import android.widget.Toast
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.songlib.core.database.model.SongEntity

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