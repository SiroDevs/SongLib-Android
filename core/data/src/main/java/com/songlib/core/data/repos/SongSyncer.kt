package com.songlib.core.data.repos

import android.util.Log
import com.songlib.core.database.daos.SongDao
import com.songlib.core.database.model.SongEntity
import com.songlib.core.network.dtos.BookSyncMeta
import com.songlib.core.network.services.SongLibService
import retrofit2.HttpException
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Downloads songs only when the server says something changed.
 *
 * 1. Ask `sync-meta` (a few hundred bytes) for each book's song count and
 *    last-updated time.
 * 2. Per book, compare with what is stored locally:
 *      - same marker and same count  -> nothing to download
 *      - marker known, count differs -> download only songs changed since the
 *        marker; if the count still differs afterwards (a deletion), re-pull
 *        just that book
 *      - never synced / empty locally -> pull the whole book
 * 3. Save the marker after EACH book, so a failed or retried sync resumes where
 *    it stopped instead of starting over.
 *
 * Books are requested one at a time so every device asks for the same URLs and
 * Vercel's edge cache can serve them.
 */
@Singleton
class SongSyncer @Inject constructor(
    private val service: SongLibService,
    private val songsDao: SongDao,
    private val prefsRepo: PrefsRepo,
) {
    data class Summary(val booksUpdated: Int, val songsDownloaded: Int)

    suspend fun syncBooks(bookIds: List<Int>): Summary {
        if (bookIds.isEmpty()) return Summary(0, 0)

        val meta = fetchMeta(bookIds)
        val markers = BookSyncMarkers.parse(prefsRepo.bookSyncMarkers)
            .filterKeys { it in bookIds }
            .toMutableMap()

        var booksUpdated = 0
        var songsDownloaded = 0

        for (bookId in bookIds) {
            val remote = meta?.firstOrNull { it.bookId == bookId }
            val downloaded = syncBook(bookId, remote, markers[bookId])
            if (downloaded > 0) booksUpdated++
            songsDownloaded += downloaded

            remote?.lastUpdated?.let { markers[bookId] = it }
            prefsRepo.bookSyncMarkers = BookSyncMarkers.serialize(markers)
        }

        Log.d(TAG, "Synced ${bookIds.size} books: $booksUpdated changed, $songsDownloaded songs downloaded")
        return Summary(booksUpdated, songsDownloaded)
    }

    /** Null means the server has no sync-meta endpoint yet; fall back to full pulls. */
    private suspend fun fetchMeta(bookIds: List<Int>): List<BookSyncMeta>? =
        try {
            service.getSyncMeta(bookIds.joinToString(",")).books
        } catch (e: HttpException) {
            if (e.code() == 404) null else throw e
        }

    private suspend fun syncBook(bookId: Int, remote: BookSyncMeta?, marker: String?): Int {
        val local = songsDao.countByBook(bookId)

        if (remote != null) {
            if (remote.songCount == 0) {
                if (local > 0) songsDao.deleteById(bookId)
                return 0
            }
            if (marker != null && marker == remote.lastUpdated && local == remote.songCount) {
                return 0
            }
        }

        var downloaded = 0
        if (remote != null && marker != null && local > 0) {
            downloaded += pullChanges(bookId, since = marker)
            if (songsDao.countByBook(bookId) == remote.songCount) return downloaded
            Log.d(TAG, "Book $bookId count mismatch after delta; re-pulling whole book")
        }
        return downloaded + pullWholeBook(bookId)
    }

    private suspend fun pullChanges(bookId: Int, since: String): Int {
        var page = 1
        var count = 0
        while (true) {
            val response = service.getSongsPage(
                bookIds = bookId.toString(), page = page, limit = PAGE_SIZE, since = since
            )
            if (response.data.isNotEmpty()) {
                songsDao.insertAll(response.data)
                count += response.data.size
            }
            if (!response.pagination.hasMore) break
            page++
        }
        return count
    }

    /**
     * Downloads every page into memory first, then swaps the book's local rows.
     * If the app dies between delete and insert, the next sync sees a count
     * mismatch and repairs it.
     */
    private suspend fun pullWholeBook(bookId: Int): Int {
        val all = mutableListOf<SongEntity>()
        var page = 1
        while (true) {
            val response = try {
                service.getSongsPage(bookIds = bookId.toString(), page = page, limit = PAGE_SIZE)
            } catch (e: HttpException) {
                if (e.code() == 404 && page == 1) break else throw e
            }
            all.addAll(response.data)
            if (!response.pagination.hasMore) break
            page++
        }
        if (all.isNotEmpty()) {
            songsDao.deleteById(bookId)
            songsDao.insertAll(all)
        }
        return all.size
    }

    private companion object {
        const val TAG = "SongSyncer"
        const val PAGE_SIZE = 500
    }
}
