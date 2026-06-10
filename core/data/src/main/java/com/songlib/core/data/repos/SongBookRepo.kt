package com.songlib.core.data.repos

import android.util.Log
import com.songlib.core.database.daos.BookDao
import com.songlib.core.database.daos.SongDao
import com.songlib.core.database.model.BookEntity
import com.songlib.core.database.model.SongEntity
import com.songlib.core.network.services.SongLibService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SongBookRepo @Inject constructor(
    private val songlibService: SongLibService,
    private var booksDao: BookDao,
    private var songsDao: SongDao,
) {
    fun fetchRemoteBooks(bookIds: Set<Int>? = null): Flow<List<BookEntity>> = flow {
        try {
            val books = songlibService.getBooks()
            if (books.isNotEmpty()) {
                val filteredBooks = if (!bookIds.isNullOrEmpty()) {
                    books.filter { it.bookId in bookIds }
                } else {
                    books
                }
                if (filteredBooks.isNotEmpty()) emit(filteredBooks) else emit(books)
            } else {
                Log.d("TAG", "⚠️ No books fetched from remote")
                emit(emptyList())
            }
        } catch (e: Exception) {
            Log.e("TAG", "❌ Error fetching books: ${e.message}", e)
            throw e
        }
    }

    /**
     * Paginated fetch — loops all pages until hasMore=false.
     * Pass [since] for delta sync (only changes after that ISO timestamp).
     */
    suspend fun fetchAndSaveSongs(bookIds: List<Int>, since: String? = null) {
        val booksParam = bookIds.joinToString(",")
        var page = 1
        var totalFetched = 0

        while (true) {
            val response = songlibService.getSongsPage(
                bookIds = booksParam,
                page    = page,
                limit   = 500,
                since   = since
            )
            val songs = response.data
            if (songs.isNotEmpty()) {
                saveSongs(songs)
                totalFetched += songs.size
                Log.d("TAG", "✅ Page $page: fetched ${songs.size} songs (total: $totalFetched)")
            }
            if (!response.pagination.hasMore) break
            page++
        }
        Log.d("TAG", "✅ All pages done. Total songs fetched: $totalFetched")
    }

    /** Flow-based paginated fetch used by SelectionScreen (no local save). */
    fun fetchRemoteSongs(bookIds: List<Int>): Flow<List<SongEntity>> = flow {
        val booksParam = bookIds.joinToString(",")
        var page = 1
        val allSongs = mutableListOf<SongEntity>()

        while (true) {
            val response = songlibService.getSongsPage(bookIds = booksParam, page = page, limit = 500)
            allSongs.addAll(response.data)
            if (!response.pagination.hasMore) break
            page++
        }
        emit(allSongs)
    }

    suspend fun saveBook(book: BookEntity) {
        withContext(Dispatchers.IO) { booksDao.insert(book) }
    }

    suspend fun saveBooks(books: List<BookEntity>) {
        if (books.isEmpty()) { Log.d("TAG", "⚠️ No books to save"); return }
        try {
            booksDao.insertAll(books)
            Log.d("TAG", "✅ ${books.size} books saved successfully")
        } catch (e: Exception) {
            Log.e("TAG", "❌ Error saving books: ${e.message}", e)
            throw e
        }
    }

    suspend fun saveSongs(songs: List<SongEntity>) {
        if (songs.isEmpty()) { Log.d("TAG", "⚠️ No songs to save"); return }
        try {
            songsDao.insertAll(songs)
            Log.d("TAG", "✅ ${songs.size} songs saved successfully")
        } catch (e: Exception) {
            Log.e("TAG", "❌ Error saving songs: ${e.message}", e)
            throw e
        }
    }

    suspend fun fetchLocalBooks(): List<BookEntity> {
        return withContext(Dispatchers.IO) { booksDao.getAll() ?: emptyList() }
    }

    suspend fun fetchLocalSongs(): List<SongEntity> {
        return withContext(Dispatchers.IO) { songsDao.getAll() ?: emptyList() }
    }

    suspend fun fetchSong(songId: Int): SongEntity {
        return withContext(Dispatchers.IO) { songsDao.getSong(songId) }
    }

    suspend fun updateSong(song: SongEntity) {
        withContext(Dispatchers.IO) { songsDao.update(song) }
    }

    suspend fun deleteById(bookId: Int) {
        withContext(Dispatchers.IO) { booksDao.deleteById(bookId) }
    }

    suspend fun deleteAllData() {
        withContext(Dispatchers.IO) {
            booksDao.deleteAll()
            songsDao.deleteAll()
        }
    }

    suspend fun deleteByBookId(bookId: Int) {
        withContext(Dispatchers.IO) { songsDao.deleteById(bookId) }
    }
}
