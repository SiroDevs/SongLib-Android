package com.songlib.domain.repository

import android.content.*
import com.songlib.data.models.*
import com.songlib.data.sources.local.*
import com.songlib.data.sources.local.daos.*
import com.songlib.data.sources.remote.ApiService
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import javax.inject.*

@Singleton
class SongBookRepository @Inject constructor(
    context: Context,
    private val apiService: ApiService,
) {
    private var bookDao: BookDao?
    private var songDao: SongDao?

    init {
        val db = AppDatabase.getDatabase(context)
        bookDao = db?.bookDao()
        songDao = db?.songDao()
    }

    fun fetchRemoteBooks(): Flow<List<Book>> = flow {
        val books = apiService.getBooks()
        emit(books)
    }

    fun fetchRemoteSongs(books: String): Flow<List<Song>> = flow {
        val songs = apiService.getSongs(books)
        emit(songs)
    }

    suspend fun saveBook(book: Book) {
        withContext(Dispatchers.IO) {
            bookDao?.insert(book)
        }
    }

    suspend fun saveSong(song: Song) {
        withContext(Dispatchers.IO) {
            songDao?.insert(song)
        }
    }

    suspend fun fetchLocalBooks(): List<Book> {
        var allBooks: List<Book>
        withContext(Dispatchers.IO) {
            allBooks = bookDao?.getAll() ?: emptyList()
        }
        return allBooks
    }

    suspend fun fetchLocalSongs(): List<Song> {
        var allSongs: List<Song>
        withContext(Dispatchers.IO) {
            allSongs = songDao?.getAll() ?: emptyList()
        }
        return allSongs
    }

    suspend fun updateSong(song: Song) {
        withContext(Dispatchers.IO) {
            songDao?.update(song)
        }
    }

    suspend fun deleteById(bookId: Int) {
        withContext(Dispatchers.IO) {
            bookDao?.deleteById(bookId)
        }
    }

    suspend fun deleteAllData() {
        withContext(Dispatchers.IO) {
            bookDao?.deleteAll()
            songDao?.deleteAll()
        }
    }

    suspend fun deleteByBookId(bookId: Int) {
        withContext(Dispatchers.IO) {
            songDao?.deleteById(bookId)
        }
    }

}