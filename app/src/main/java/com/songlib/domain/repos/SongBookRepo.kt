package com.songlib.domain.repos

import android.content.*
import android.util.Log
import com.songlib.data.models.*
import com.songlib.data.sources.local.*
import com.songlib.data.sources.local.daos.*
import com.songlib.data.sources.remote.ApiService
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import javax.inject.*
import kotlin.collections.isNotEmpty
import kotlin.collections.map

@Singleton
class SongBookRepo @Inject constructor(
    context: Context,
    private val apiService: ApiService,
) {
    private var booksDao: BookDao?
    private var songsDao: SongDao?

    init {
        val db = AppDatabase.getDatabase(context)
        booksDao = db?.booksDao()
        songsDao = db?.songsDao()
    }

    fun fetchRemoteBooks(): Flow<List<Book>> = flow {
        try {
            val books = apiService.getBooks()
            if (books.isNotEmpty()) {
                emit(books)
            } else {
                Log.d("TAG", "⚠️ No books fetched from remote")
                emit(emptyList())
            }
        } catch (e: Exception) {
            Log.e("TAG", "❌ Error fetching books: ${e.message}", e)
            throw e
        }
    }

    suspend fun fetchAndSaveSongs(bookIds: List<Int>) {
        try {
            val booksParam = bookIds.joinToString(",")
            val songs = apiService.getSongs(booksParam)
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

    suspend fun saveBook(book: Book) {
        withContext(Dispatchers.IO) {
            booksDao?.insert(book)
        }
    }

    suspend fun saveBooks(books: List<Book>) {
        if (books.isEmpty()) {
            Log.d("TAG", "⚠️ No books to save")
            return
        }

        try {
            booksDao?.insertAll(books)
            Log.d("TAG", "✅ ${books.size} books saved successfully")
        } catch (e: Exception) {
            Log.e("TAG", "❌ Error saving books: ${e.message}", e)
            throw e
        }
    }

    suspend fun saveSongs(songs: List<Song>) {
        if (songs.isEmpty()) {
            Log.d("TAG", "⚠️ No songs to save")
            return
        }

        try {
            songsDao?.insertAll(songs)
            Log.d("TAG", "✅ ${songs.size} songs saved successfully")
        } catch (e: Exception) {
            Log.e("TAG", "❌ Error saving songs: ${e.message}", e)
            throw e
        }
    }

    suspend fun fetchLocalBooks(): List<Book> {
        var allBooks: List<Book>
        withContext(Dispatchers.IO) {
            allBooks = booksDao?.getAll() ?: emptyList()
        }
        return allBooks
    }

    suspend fun fetchLocalSongs(): List<Song> {
        var allSongs: List<Song>
        withContext(Dispatchers.IO) {
            allSongs = songsDao?.getAll() ?: emptyList()
        }
        return allSongs
    }

    suspend fun fetchSong(songId: Int): Song {
        var song: Song
        withContext(Dispatchers.IO) {
            song = songsDao?.getSong(songId)!!
        }
        return song
    }

    suspend fun updateSong(song: Song) {
        withContext(Dispatchers.IO) {
            songsDao?.update(song)
        }
    }

    suspend fun deleteById(bookId: Int) {
        withContext(Dispatchers.IO) {
            booksDao?.deleteById(bookId)
        }
    }

    suspend fun deleteAllData() {
        withContext(Dispatchers.IO) {
            booksDao?.deleteAll()
            songsDao?.deleteAll()
        }
    }

    suspend fun deleteByBookId(bookId: Int) {
        withContext(Dispatchers.IO) {
            songsDao?.deleteById(bookId)
        }
    }

}