package com.songlib.feature.edits.user.view

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.Modifier
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.songlib.core.common.utils.Routes
import com.songlib.core.data.repos.PrefsRepo
import com.songlib.core.database.model.EditEntity
import com.songlib.core.ui.components.action.AppTopBar
import com.songlib.core.ui.components.indicators.EmptyState
import com.songlib.feature.edits.user.viewmodel.EditsViewModel

@Composable
fun EditsScreen(
    navController: NavHostController,
    prefsRepo: PrefsRepo,
    viewModel: EditsViewModel = hiltViewModel(),
) {
    val edits by viewModel.edits.collectAsState()
    val isAdmin = remember { prefsRepo.isAdmin }

    LaunchedEffect(Unit) { viewModel.load() }

    Scaffold(
        topBar = {
            AppTopBar(
                title      = "My Songs Edits",
                showGoBack = true,
                onNavIconClick = { navController.popBackStack() }
            )
        },
        floatingActionButton = {
            if (isAdmin) {
                ExtendedFloatingActionButton(
                    onClick = { navController.navigate(Routes.ADMIN_EDITS) },
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                    contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
                    icon = {
                        Icon(
                            Icons.Default.AdminPanelSettings,
                            contentDescription = "Admin panel",
                        )
                    },
                    text = { Text("Switch to Admin") },
                )
            }
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize()) {
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
