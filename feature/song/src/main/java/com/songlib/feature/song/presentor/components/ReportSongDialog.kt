package com.songlib.feature.song.presentor.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

private val REPORT_TYPES = listOf(
    "typo"          to "Typo / Spelling error",
    "missing_verse" to "Missing verse",
    "wrong_song"    to "Wrong song content",
    "wrong_number"  to "Wrong song number",
    "other"         to "Other"
)

@Composable
fun ReportSongDialog(
    songTitle: String,
    isSubmitting: Boolean,
    onDismiss: () -> Unit,
    onSubmit: (type: String, description: String) -> Unit,
) {
    var selectedType by remember { mutableStateOf(REPORT_TYPES[0].first) }
    var description  by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Report: $songTitle") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text("Issue type", style = MaterialTheme.typography.labelMedium)
                Spacer(Modifier.height(4.dp))
                REPORT_TYPES.forEach { (key, label) ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        RadioButton(
                            selected = selectedType == key,
                            onClick  = { selectedType = key }
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(label, style = MaterialTheme.typography.bodyMedium)
                    }
                }
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value         = description,
                    onValueChange = { description = it },
                    label         = { Text("Description (optional)") },
                    modifier      = Modifier.fillMaxWidth(),
                    minLines      = 3,
                )
            }
        },
        confirmButton = {
            Button(
                onClick  = { onSubmit(selectedType, description) },
                enabled  = !isSubmitting
            ) {
                if (isSubmitting) {
                    CircularProgressIndicator(modifier = Modifier.size(16.dp))
                } else {
                    Text("Submit")
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
