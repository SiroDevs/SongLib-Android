package com.songlib.feature.history.view.tabs

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.songlib.core.database.model.SearchEntity
import com.songlib.core.ui.components.indicators.EmptyState
import com.songlib.feature.history.utils.HistoryGrouper
import com.songlib.feature.history.utils.HistoryRow

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SearchesTab(
    searches: List<SearchEntity>,
    selectedIds: Set<Int>,
    showOlder: Boolean,
    onToggleOlder: () -> Unit,
    onItemClick: (SearchEntity) -> Unit,
    onItemLongClick: (SearchEntity) -> Unit,
) {
    if (searches.isEmpty()) {
        EmptyState(message = "No searches yet")
        return
    }

    val (rows, hasOlder) = remember(searches, showOlder) {
        HistoryGrouper.group(
            items        = searches,
            dateSelector = { it.created },
            showOlder    = showOlder,
        )
    }

    LazyColumn {
        rows.forEach { row ->
            when (row) {
                is HistoryRow.Header -> {
                    stickyHeader(key = "header_${row.bucket.label}") {
                        SectionHeader(label = row.bucket.label)
                    }
                }

                is HistoryRow.Item -> {
                    item(key = "search_${row.data.id}") {
                        val isSelected = row.data.id in selectedIds

                        Box(
                            modifier = Modifier
                                .combinedClickable(
                                    onClick     = { onItemClick(row.data) },
                                    onLongClick = { onItemLongClick(row.data) },
                                )
                                .background(
                                    if (isSelected) MaterialTheme.colorScheme.primaryContainer
                                    else Color.Transparent
                                )
                        ) {
                            ListItem(
                                headlineContent = { Text(row.data.title) },
                                supportingContent = {
                                    Text(
                                        text  = row.timestamp,     // "Just now", "3 mins ago", "11:23 AM"
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    )
                                },
                                trailingContent = {
                                    Text(
                                        text  = "${row.data.hits} hit${if (row.data.hits != 1) "s" else ""}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.primary,
                                    )
                                },
                            )
                        }
                        HorizontalDivider()
                    }
                }
            }
        }

        if (hasOlder) {
            item(key = "go_further_back_searches") {
                GoFurtherBackButton(
                    expanded = showOlder,
                    onClick  = onToggleOlder,
                )
            }
        }
    }
}