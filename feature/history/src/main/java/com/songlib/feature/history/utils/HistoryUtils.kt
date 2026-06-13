package com.songlib.feature.history.utils

import com.songlib.core.database.model.HistoryEntity
import com.songlib.core.database.model.SongEntity
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

fun relativeTime(dateString: String): String {
    val fmt = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)
    val date = runCatching { fmt.parse(dateString) }.getOrNull() ?: return dateString
    val diffMs = System.currentTimeMillis() - date.time
    val diffSec = diffMs / 1_000
    val diffMin = diffMs / 60_000

    return when {
        diffSec < 60 -> "Just now"
        diffMin == 1L -> "A minute ago"
        diffMin < 60 -> "$diffMin minutes ago"
        diffMin < 120 -> "An hour ago"
        else -> {
            // Show actual time e.g. "11:23 AM"
            SimpleDateFormat("h:mm a", Locale.US).format(date)
        }
    }
}

sealed class HistoryBucket(val label: String) {
    object Today : HistoryBucket("Today")
    object Yesterday : HistoryBucket("Yesterday")
    object Last7Days : HistoryBucket("Last 7 days")
    object Last28Days : HistoryBucket("Last 28 days")
    class Month(label: String) : HistoryBucket(label)   // "June", "May", …
}

/**
 * A flat list of display rows, each either a sticky header or a data item.
 * [T] is SongEntity or SearchEntity.
 */
sealed class HistoryRow<out T> {
    data class Header<T>(val bucket: HistoryBucket) : HistoryRow<T>()
    data class Item<T>(val data: T, val timestamp: String, val bucket: HistoryBucket) :
        HistoryRow<T>()
}

object HistoryGrouper {

    private val fmt = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)

    /**
     * Groups a list of items into recent buckets (Today … Last 28 days) and
     * optionally older month buckets.
     *
     * @param items          raw list, newest-first preferred but order doesn't matter
     * @param dateSelector   extract the "created" string from an item
     * @param showOlder      when true, month-grouped items beyond 28 days are included
     * @param hasOlderItems  output: whether any items beyond 28 days exist at all
     */
    fun <T> group(
        items: List<T>,
        dateSelector: (T) -> String,
        showOlder: Boolean,
    ): Pair<List<HistoryRow<T>>, Boolean> {

        val now = Calendar.getInstance()
        val today = now.dayOfYear to now.get(Calendar.YEAR)

        fun parse(s: String): Date? = runCatching { fmt.parse(s) }.getOrNull()

        fun bucket(date: Date): HistoryBucket {
            val cal = Calendar.getInstance().apply { time = date }
            val daysDiff = daysBetween(cal, now)
            return when {
                daysDiff == 0 -> HistoryBucket.Today
                daysDiff == 1 -> HistoryBucket.Yesterday
                daysDiff < 7 -> HistoryBucket.Last7Days
                daysDiff < 28 -> HistoryBucket.Last28Days
                else -> {
                    val monthName = SimpleDateFormat("MMMM yyyy", Locale.US).format(date)
                    HistoryBucket.Month(monthName)
                }
            }
        }

        // Sort newest-first
        val sorted = items.sortedByDescending { parse(dateSelector(it))?.time ?: 0L }

        val recentRows = mutableListOf<HistoryRow<T>>()
        val olderRows = mutableListOf<HistoryRow<T>>()
        var lastBucket: HistoryBucket? = null

        for (item in sorted) {
            val date = parse(dateSelector(item)) ?: continue
            val b = bucket(date)
            val ts = relativeTime(dateSelector(item))

            val isOlder = b is HistoryBucket.Month

            val targetList = if (isOlder) olderRows else recentRows

            if (b.label != lastBucket?.label) {
                targetList += HistoryRow.Header(b)
                lastBucket = b
            }
            targetList += HistoryRow.Item(item, ts, b)
        }

        // Reset lastBucket tracker between recent/older so headers work correctly
        lastBucket = null
        val rebuiltOlder = mutableListOf<HistoryRow<T>>()
        for (row in olderRows) {
            when (row) {
                is HistoryRow.Header -> {
                    if (row.bucket.label != lastBucket?.label) {
                        rebuiltOlder += row
                        lastBucket = row.bucket
                    }
                }

                is HistoryRow.Item -> rebuiltOlder += row
            }
        }

        val hasOlder = rebuiltOlder.isNotEmpty()
        val finalRows = if (showOlder) recentRows + rebuiltOlder else recentRows

        return finalRows to hasOlder
    }

    private fun daysBetween(from: Calendar, to: Calendar): Int {
        val f = from.clone() as Calendar
        val t = to.clone() as Calendar
        // Normalise to midnight
        f.set(Calendar.HOUR_OF_DAY, 0); f.set(Calendar.MINUTE, 0)
        f.set(Calendar.SECOND, 0); f.set(Calendar.MILLISECOND, 0)
        t.set(Calendar.HOUR_OF_DAY, 0); t.set(Calendar.MINUTE, 0)
        t.set(Calendar.SECOND, 0); t.set(Calendar.MILLISECOND, 0)
        val diff = t.timeInMillis - f.timeInMillis
        return (diff / 86_400_000).toInt()
    }

    private val Calendar.dayOfYear get() = get(Calendar.DAY_OF_YEAR)
}

data class SongView(
    val song: HistoryEntity,
    val entity: SongEntity,
)