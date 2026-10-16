package com.songlib.core.data.repos

import com.songlib.core.common.utils.AppLimits

/**
 * Pure helpers for the two small pieces of sync state kept in preferences.
 */
object BookSyncMarkers {
    private const val ENTRY_SEP = ";"
    private const val KV_SEP = "|"

    /** "1|2026-10-01T10:00:00.000Z;3|..." -> {1: "2026-10-01T10:00:00.000Z", 3: ...} */
    fun parse(raw: String): Map<Int, String> =
        raw.split(ENTRY_SEP)
            .mapNotNull { entry ->
                val parts = entry.split(KV_SEP, limit = 2)
                val id = parts.getOrNull(0)?.trim()?.toIntOrNull()
                val marker = parts.getOrNull(1)?.trim()
                if (id != null && !marker.isNullOrEmpty()) id to marker else null
            }
            .toMap()

    fun serialize(markers: Map<Int, String>): String =
        markers.entries.joinToString(ENTRY_SEP) { "${it.key}$KV_SEP${it.value}" }
}

/**
 * Songbook selection rules. The cap lives here (app side) rather than in the
 * API because ChurchLib shares the same backend.
 */
object BookSelection {
    fun parse(csv: String): List<Int> =
        csv.split(",").mapNotNull { it.trim().toIntOrNull() }.distinct()

    /** Keeps at most [AppLimits.MAX_SONGBOOKS] ids, preserving order. */
    fun cap(csv: String): String =
        parse(csv).take(AppLimits.MAX_SONGBOOKS).joinToString(",")
}
