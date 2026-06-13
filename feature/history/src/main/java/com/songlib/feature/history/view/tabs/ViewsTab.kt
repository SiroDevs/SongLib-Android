package com.songlib.feature.history.view.tabs

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.songlib.core.database.model.BookEntity
import com.songlib.core.ui.components.indicators.EmptyState
import com.songlib.core.ui.components.listitems.SongItem
import com.songlib.feature.history.utils.HistoryGrouper
import com.songlib.feature.history.utils.HistoryRow
import com.songlib.feature.history.utils.SongView

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ViewsTab(
    views: List<SongView>,
    bookMap: Map<Int, BookEntity>,
    selectedIds: Set<Int>,
    showOlder: Boolean,
    onToggleOlder: () -> Unit,
    onItemClick: (SongView) -> Unit,
    onItemLongClick: (SongView) -> Unit,
) {
    if (views.isEmpty()) {
        EmptyState(message = "No songs viewed yet")
        return
    }

    val (rows, hasOlder) = remember(views, showOlder) {
        HistoryGrouper.group(
            items        = views,
            dateSelector = { it.song.created },  // HistoryEntity.created carries the real timestamp
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
                    // Key on history id so duplicate views of the same song each get their own row
                    item(key = "song_${row.data.song.id}") {
                        val bookTitle  = bookMap[row.data.entity.book]?.title ?: ""
                        val isSelected = row.data.song.id in selectedIds

                        Box(
                            modifier = Modifier
                                .combinedClickable(
                                    onClick     = { onItemClick(row.data) },
                                    onLongClick = { onItemLongClick(row.data) },
                                )
                                .background(
                                    if (isSelected) MaterialTheme.colorScheme.primaryContainer
                                    else androidx.compose.ui.graphics.Color.Transparent
                                )
                        ) {
                            SongItem(
                                song           = row.data.entity,
                                showLike       = false,
                                customTitle    = "${row.data.entity.songNo}. ${row.data.entity.title}",
                                customSubtitle = bookTitle,
                                trailingLabel  = row.timestamp,
                            )
                        }
                        HorizontalDivider()
                    }
                }
            }
        }

        // "Go further back" footer — only shown when older items exist and aren't shown yet
        if (hasOlder) {
            item(key = "go_further_back_views") {
                GoFurtherBackButton(
                    expanded = showOlder,
                    onClick  = onToggleOlder,
                )
            }
        }
    }
}

// ── Shared composables ────────────────────────────────────────────────────────

@Composable
internal fun SectionHeader(label: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(horizontal = 16.dp, vertical = 6.dp)
    ) {
        Text(
            text       = label,
            style      = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold,
            fontSize   = 12.sp,
            color      = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
internal fun GoFurtherBackButton(expanded: Boolean, onClick: () -> Unit) {
    TextButton(
        onClick  = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector        = Icons.Default.ExpandMore,
                contentDescription = null,
                modifier           = Modifier.size(18.dp),
                tint               = MaterialTheme.colorScheme.primary,
            )
            Spacer(Modifier.width(6.dp))
            Text(
                text  = if (expanded) "Show less" else "Go further back",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
            )
        }
    }
}