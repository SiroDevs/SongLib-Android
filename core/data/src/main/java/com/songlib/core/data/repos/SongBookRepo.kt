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
import kotlin.collections.isNotEmpty

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

    fun fetchRemoteSongs(bookIds: List<Int>): Flow<List<SongEntity>> = flow {
        try {
            val booksParam = bookIds.joinToString(",")
            val songs = songlibService.getSongs(booksParam)
            if (songs.isNotEmpty()) {
                emit(songs)
            } else {
                Log.d("TAG", "⚠️ No songs fetched from remote")
                emit(emptyList())
            }
        } catch (e: Exception) {
            Log.e("TAG", "❌ Error fetching songs: ${e.message}", e)
            throw e
        }
    }

    suspend fun fetchAndSaveSongs(bookIds: List<Int>) {
        try {
            val booksParam = bookIds.joinToString(",")
            val songs = songlibService.getSongs(booksParam)
            Log.d("TAG", "✅ ${songs.size} songs fetched for books: $booksParam")

            if (songs.isNotEmpty()) {
                saveSongs(songs)
            } else {
                Log.d("TAG", "⚠️ No songs fetched from remote")
            }
        } catch (e: Exception) {
            Log.e("TAG", "❌ Error fetching songs: ${e.message}", e)
        }
    }

    suspend fun saveBook(book: BookEntity) {
        withContext(Dispatchers.IO) {
            booksDao.insert(book)
        }
    }

    suspend fun saveBooks(books: List<BookEntity>) {
        if (books.isEmpty()) {
            Log.d("TAG", "⚠️ No books to save")
            return
        }

        try {
            booksDao.insertAll(books)
            Log.d("TAG", "✅ ${books.size} books saved successfully")
        } catch (e: Exception) {
            Log.e("TAG", "❌ Error saving books: ${e.message}", e)
            throw e
        }
    }

    suspend fun saveSongs(songs: List<SongEntity>) {
        if (songs.isEmpty()) {
            Log.d("TAG", "⚠️ No songs to save")
            return
        }

        try {
            songsDao.insertAll(songs)
            Log.d("TAG", "✅ ${songs.size} songs saved successfully")
        } catch (e: Exception) {
            Log.e("TAG", "❌ Error saving songs: ${e.message}", e)
            throw e
        }
    }

    suspend fun fetchLocalBooks(): List<BookEntity> {
        var allBooks: List<BookEntity>
        withContext(Dispatchers.IO) {
            allBooks = booksDao.getAll() ?: emptyList()
        }
        return allBooks
    }

    suspend fun fetchLocalSongs(): List<SongEntity> {
        var allSongs: List<SongEntity>
        withContext(Dispatchers.IO) {
            allSongs = songsDao.getAll() ?: emptyList()
        }
        return allSongs
    }

    suspend fun fetchSong(songId: Int): SongEntity {
        var song: SongEntity
        withContext(Dispatchers.IO) {
            song = songsDao.getSong(songId)
        }
        return song
    }

    suspend fun updateSong(song: SongEntity) {
        withContext(Dispatchers.IO) {
            songsDao.update(song)
        }
    }

    suspend fun deleteById(bookId: Int) {
        withContext(Dispatchers.IO) {
            booksDao.deleteById(bookId)
        }
    }

    suspend fun deleteAllData() {
        withContext(Dispatchers.IO) {
            booksDao.deleteAll()
            songsDao.deleteAll()
        }
    }

    suspend fun deleteByBookId(bookId: Int) {
        withContext(Dispatchers.IO) {
            songsDao.deleteById(bookId)
        }
    }

}