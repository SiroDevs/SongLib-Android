package com.songlib.feature.home.view

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.songlib.core.database.model.EditEntity
import com.songlib.core.ui.components.action.AppTopBar
import com.songlib.core.ui.components.indicators.EmptyState
import com.songlib.feature.home.MyEditsViewModel

@Composable
fun MyEditsScreen(
    navController: NavHostController,
    viewModel: MyEditsViewModel = hiltViewModel(),
) {
    val edits by viewModel.edits.collectAsState()
    LaunchedEffect(Unit) { viewModel.load() }

    Scaffold(
        topBar = {
            AppTopBar(
                title      = "My Edits",
                showGoBack = true,
                onNavIconClick = { navController.popBackStack() }
            )
        }
    ) { padding ->
        if (edits.isEmpty()) {
            EmptyState(message = "No edits submitted yet")
        } else {
            LazyColumn(contentPadding = padding) {
                items(edits, key = { it.id }) { edit ->
                    EditListItem(edit = edit)
                    HorizontalDivider()
                }
            }
        }
    }
}

@Composable
fun EditListItem(edit: EditEntity) {
    val (statusColor, statusLabel) = when (edit.status) {
        "accepted" -> MaterialTheme.colorScheme.primary to "✅ Accepted"
        "rejected" -> MaterialTheme.colorScheme.error   to "❌ Rejected"
        else       -> MaterialTheme.colorScheme.secondary to "⏳ Pending"
    }

    ListItem(
        headlineContent   = { Text(edit.title, maxLines = 1) },
        supportingContent = { Text("Song ID: ${edit.songId}") },
        trailingContent   = {
            Text(
                text  = statusLabel,
                color = statusColor,
                style = MaterialTheme.typography.labelMedium
            )
        }
    )
}
