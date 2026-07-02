package com.songlib.feature.song.editor.view

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.songlib.core.database.model.DraftEntity
import com.songlib.core.database.model.SongEntity
import com.songlib.core.ui.components.action.AppTopBar
import com.songlib.feature.song.editor.EditSubmitState
import com.songlib.feature.song.editor.EditorViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditorScreen(
    navController: NavHostController,
    song: SongEntity? = null,
    draft: DraftEntity? = null,
    viewModel: EditorViewModel = hiltViewModel(),
) {
    val submitState by viewModel.submitState.collectAsState()
    val titleField by viewModel.title.collectAsState()
    val contentField by viewModel.content.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(song, draft) {
        when {
            song != null -> viewModel.initWithSong(song)
            draft != null -> viewModel.initWithDraft(draft)
        }
    }

    LaunchedEffect(Unit) {
        viewModel.toastEvent.collect { msg ->
            Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
        }
    }

    LaunchedEffect(submitState) {
        if (submitState is EditSubmitState.Success) {
            viewModel.resetState()
            navController.popBackStack()
        }
    }

    val isSubmitting = submitState is EditSubmitState.Submitting

    val screenTitle = if (draft != null) "Edit Draft" else "Edit Song"
    val tagline = draft?.title ?: song?.title ?: ""

    Scaffold(
        topBar = {
            AppTopBar(
                title = screenTitle,
                tagline = tagline,
                showGoBack = true,
                onNavIconClick = { navController.popBackStack() },
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { if (!isSubmitting) viewModel.submit() },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
            ) {
                Icon(Icons.Default.Check, contentDescription = "Save")
            }
        }
    ) { paddingValues ->

        if (isSubmitting) {
            //loading implementation
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
                .imePadding()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(
                text = if (draft != null)
                    "Changes are saved locally to your drafts."
                else
                    "Your changes will be saved locally right away. " +
                            "They will be reviewed before being merged into the public library.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 8.dp),
            )

            OutlinedTextField(
                value = titleField,
                onValueChange = viewModel::onTitleChange,
                label = { Text("Title") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                enabled = !isSubmitting,
            )

            OutlinedTextField(
                value = contentField,
                onValueChange = viewModel::onContentChange,
                label = { Text("Lyrics") },
                minLines = 10,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 80.dp),
                enabled = !isSubmitting,
            )
        }
    }
}
