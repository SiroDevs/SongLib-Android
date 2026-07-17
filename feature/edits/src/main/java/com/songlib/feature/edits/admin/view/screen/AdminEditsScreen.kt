package com.songlib.feature.edits.admin.view.screen

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.songlib.core.network.dtos.EditDto
import com.songlib.core.ui.components.action.AppTopBar
import com.songlib.core.ui.components.indicators.EmptyState
import com.songlib.core.ui.components.indicators.ErrorState
import com.songlib.feature.edits.admin.viewmodel.AdminEditsUiState
import com.songlib.feature.edits.admin.viewmodel.AdminEditsViewModel
import com.songlib.feature.edits.admin.view.components.PendingEditCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminEditsScreen(
    navController: NavHostController,
    viewModel: AdminEditsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val pendingAction by viewModel.pendingAction.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.load()
        viewModel.toastEvent.collect { msg ->
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
        }
    }

    var rejectTarget by remember { mutableStateOf<EditDto?>(null) }
    var rejectReason by remember { mutableStateOf("") }

    if (rejectTarget != null) {
        AlertDialog(
            onDismissRequest = { rejectTarget = null; rejectReason = "" },
            title = { Text("Reject Edit") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        "Optionally leave a reason for ${rejectTarget!!.title}:",
                        style = MaterialTheme.typography.bodyMedium,
                    )
                    OutlinedTextField(
                        value = rejectReason,
                        onValueChange = { rejectReason = it },
                        label = { Text("Reason (optional)") },
                        minLines = 2,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.reject(rejectTarget!!.editId, rejectReason.takeIf { it.isNotBlank() })
                    rejectTarget = null
                    rejectReason = ""
                }) { Text("Reject", color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = {
                TextButton(onClick = { rejectTarget = null; rejectReason = "" }) {
                    Text("Cancel")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Admin — Pending Edits",
                showGoBack = true,
                onNavIconClick = { navController.popBackStack() },
            )
        }
    ) { padding ->
        when (val state = uiState) {
            AdminEditsUiState.Loading -> {}

            AdminEditsUiState.Empty ->
                EmptyState(message = "No pending edits — you're all caught up 🎉")

            is AdminEditsUiState.Error ->
                ErrorState(message = state.message, retryAction = { viewModel.load() })

            is AdminEditsUiState.Loaded ->
                LazyColumn(
                    contentPadding = padding,
                    modifier = Modifier.fillMaxSize(),
                ) {
                    items(state.edits, key = { it.editId }) { edit ->
                        PendingEditCard(
                            edit = edit,
                            isProcessing = pendingAction.containsKey(edit.editId),
                            onApprove = { viewModel.approve(edit.editId) },
                            onReject = {
                                rejectTarget = edit
                                rejectReason = ""
                            },
                        )
                        HorizontalDivider()
                    }
                }
        }
    }
}
