package com.songlib.feature.home.view

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.material3.ListItem
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.songlib.core.database.model.DraftEntity
import com.songlib.core.ui.components.action.AppTopBar
import com.songlib.core.ui.components.indicators.EmptyState
import com.songlib.feature.home.DraftsViewModel

@Composable
fun DraftsScreen(
    navController: NavHostController,
    viewModel: DraftsViewModel = hiltViewModel(),
) {
    val drafts by viewModel.drafts.collectAsState()
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

    Scaffold(
        topBar = {
            AppTopBar(
                title      = "My Drafts",
                showGoBack = true,
                onNavIconClick = { navController.popBackStack() }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showNewDraftDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "New draft")
            }
        }
    ) { padding ->
        if (drafts.isEmpty()) {
            EmptyState(message = "No drafts yet.\nTap + to write one.")
        } else {
            LazyColumn(contentPadding = padding) {
                items(drafts, key = { it.id }) { draft ->
                    DraftListItem(
                        draft    = draft,
                        onDelete = { viewModel.deleteDraft(draft.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun DraftListItem(draft: DraftEntity, onDelete: () -> Unit) {
    ListItem(
        headlineContent = { Text(draft.title, maxLines = 1, overflow = TextOverflow.Ellipsis) },
        supportingContent = {
            if (draft.content.isNotEmpty()) {
                Text(
                    text     = draft.content.take(80),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        },
        trailingContent = {
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Delete draft")
            }
        }
    )
    HorizontalDivider()
}

@Composable
fun NewDraftDialog(
    onDismiss: () -> Unit,
    onConfirm: (title: String, content: String) -> Unit,
) {
    var title   by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("New Draft") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value         = title,
                    onValueChange = { title = it },
                    label         = { Text("Song title") },
                    modifier      = Modifier.fillMaxWidth(),
                    singleLine    = true,
                )
                OutlinedTextField(
                    value         = content,
                    onValueChange = { content = it },
                    label         = { Text("Lyrics / content") },
                    modifier      = Modifier.fillMaxWidth(),
                    minLines      = 4,
                )
            }
        },
        confirmButton = {
            Button(
                onClick  = { if (title.isNotBlank()) onConfirm(title, content) },
                enabled  = title.isNotBlank()
            ) { Text("Save") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
