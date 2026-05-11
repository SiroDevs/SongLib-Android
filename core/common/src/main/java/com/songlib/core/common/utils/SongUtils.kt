package com.songlib.core.utils

import com.songlib.data.models.Song

object SongUtils {

    fun searchSongs(
        allSongs: List<Song>,
        qry: String,
        byNo: Boolean = false
    ): List<Song> {
        val query = qry.trim()

        if (query.isBlank()) {
            return allSongs
        }

        if (byNo) {
            val number = query.toIntOrNull()
            return if (number != null) {
                allSongs.filter { it.songNo == number }
            } else {
                emptyList()
            }
        }

        val charsPattern = "[!,]".toRegex()
        val words = if (query.contains(',')) {
            query.split(',').map { it.trim() }
        } else {
            listOf(query.lowercase())
        }

        val queryPattern =
            words.joinToString(".*") { Regex.escape(it) }.toRegex(RegexOption.IGNORE_CASE)

        return allSongs.filter { song ->
            val title = song.title.replace(charsPattern, "").lowercase()
            val alias = song.alias.replace(charsPattern, "").lowercase()
            val content = song.content.replace(charsPattern, "").lowercase()

            queryPattern.containsMatchIn(title)
                    || queryPattern.containsMatchIn(alias)
                    || queryPattern.containsMatchIn(content)
        }
    }
}
