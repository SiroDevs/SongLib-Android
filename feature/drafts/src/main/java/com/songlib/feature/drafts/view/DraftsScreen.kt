package com.songlib.feature.drafts.view

import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.songlib.core.common.utils.Routes
import com.songlib.core.database.model.DraftEntity
import com.songlib.core.database.model.SongEntity
import com.songlib.core.ui.components.action.AppTopBar
import com.songlib.core.ui.components.indicators.EmptyState
import com.songlib.core.ui.components.listitems.SongItem
import com.songlib.feature.drafts.DraftsViewModel

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DraftsScreen(
    navController: NavHostController,
    viewModel: DraftsViewModel = hiltViewModel(),
) {
    val drafts by viewModel.drafts.collectAsState()
    val selectedIds by viewModel.selectedIds.collectAsState()
    var showNewDraftDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.toastEvent.collect {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
        }
    }

    if (showNewDraftDialog) {
        NewDraftDialog(
            onDismiss = { showNewDraftDialog = false },
            onConfirm = { title, content ->
                viewModel.saveDraft(title, content)
                showNewDraftDialog = false
            }
        )
    }

    val topBarTitle = if (selectedIds.isNotEmpty()) "${selectedIds.size} selected" else "My Drafts"

    Scaffold(
        topBar = {
            AppTopBar(
                title = topBarTitle,
                showGoBack = true,
                onNavIconClick = {
                    if (selectedIds.isNotEmpty()) viewModel.clearSelection()
                    else navController.popBackStack()
                },
                actions = {
                    if (selectedIds.isNotEmpty()) {
                        IconButton(onClick = { viewModel.deleteSelected() }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete selected")
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            // Hide FAB when in selection mode
            if (selectedIds.isEmpty()) {
                FloatingActionButton(onClick = { showNewDraftDialog = true }) {
                    Icon(Icons.Default.Add, contentDescription = "New draft")
                }
            }
        }
    ) { padding ->
        if (drafts.isEmpty()) {
            EmptyState(message = "No drafts yet.\nTap + to write one.")
        } else {
            LazyColumn(contentPadding = padding) {
                items(drafts, key = { it.id }) { draft ->
                    val isSelected = selectedIds.contains(draft.id)
                    Box(
                        modifier = Modifier
                            .combinedClickable(
                                onClick = {
                                    if (selectedIds.isNotEmpty()) {
                                        viewModel.toggleSelection(draft.id)
                                    } else {
                                        // Navigate to draft presenter
                                        navController.currentBackStackEntry
                                            ?.savedStateHandle
                                            ?.set("draft", draft)
                                        navController.navigate(Routes.DRAFT_PRESENT)
                                    }
                                },
                                onLongClick = { viewModel.toggleSelection(draft.id) }
                            )
                            .background(
                                if (isSelected) MaterialTheme.colorScheme.primaryContainer
                                else Color.Transparent
                            )
                    ) {
                        // Reuse SongItem: showLike=false, showSongNo=false
                        // title  = draft title
                        // subtitle = first line of content or "(empty)"
                        val subtitle = draft.content
                            .split("##").firstOrNull()
                            ?.split("#")?.firstOrNull()
                            ?.trim()
                            ?.takeIf { it.isNotEmpty() } ?: "(no lyrics)"

                        SongItem(
                            song = draft.toFakeSongEntity(),
                            showLike = false,
                            showSongNo = false,
                            customTitle = draft.title,
                            customSubtitle = subtitle,
                        )
                    }
                    HorizontalDivider()
                }
            }
        }
    }
}

/**
 * Converts a [DraftEntity] to a [SongEntity] stub so we can reuse [SongItem]
 * without any changes to the shared component.
 * The fields that SongItem actually renders are overridden via customTitle/customSubtitle.
 */
private fun DraftEntity.toFakeSongEntity() = SongEntity(
    songId = id,
    alias = "",
    book = book ?: 0,
    content = content,
    created = created,
    liked = false,
    likes = 0,
    songNo = songNo ?: 0,
    title = title,
    views = 0,
)

@Composable
fun NewDraftDialog(
    onDismiss: () -> Unit,
    onConfirm: (title: String, content: String) -> Unit,
) {
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("New Draft") },
        text = {
            Column(verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Song title") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                )
                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    label = { Text("Lyrics / content") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 4,
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { if (title.isNotBlank()) onConfirm(title, content) },
                enabled = title.isNotBlank()
            ) { Text("Save") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
