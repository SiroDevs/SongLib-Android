package com.songlib.feature.home.utils

import com.songlib.core.database.model.BookEntity
import com.songlib.core.database.model.SongEntity

object HomeUtils {
    fun filterSongsForBook(
        songs: List<SongEntity>,
        books: List<BookEntity>,
        bookIndex: Int,
    ): List<SongEntity> = when (bookIndex) {
        -1 -> songs
        in books.indices -> {
            val bookId = books[bookIndex].bookId
            songs.filter { it.book == bookId }
        }

        else -> songs
    }

    fun applyLikeToggle(
        list: List<SongEntity>,
        updatedIds: Set<Int>,
    ): List<SongEntity> = list.map { s ->
        if (s.songId in updatedIds) s.copy(liked = !s.liked) else s
    }

    fun buildLikeToastMessage(count: Int, wasAllLiked: Boolean): String {
        val noun = if (count == 1) "song" else "$count songs"
        return if (wasAllLiked) "Removed $noun from likes"
        else "Added $noun to likes ❤️"
    }
}