package com.songlib.domain.repository

import android.content.*
import com.songlib.data.models.*
import com.songlib.data.sources.local.*
import com.songlib.data.sources.local.daos.SongDao
import com.songlib.data.sources.remote.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
import javax.inject.*

@Singleton
class SongRepository @Inject constructor(
    context: Context,
    private val apiService: ApiService,
) {
    private var songDao: SongDao?

    init {
        val db = AppDatabase.getDatabase(context)
        songDao = db?.songDao()
    }

    fun getSongs(books: String): Flow<List<Song>> = flow {
        val songs = apiService.getSongs(books)
        emit(songs)
    }

    suspend fun deleteAllSongs() {
        withContext(Dispatchers.IO) {
            songDao?.deleteAll()
        }
    }

    suspend fun deleteByBookId(bookId: Int) {
        withContext(Dispatchers.IO) {
            songDao?.deleteByBookId(bookId)
        }
    }

    suspend fun saveSong(song: Song) {
        withContext(Dispatchers.IO) {
            songDao?.insert(song)
        }
    }

    suspend fun updateSong(song: Song) {
        withContext(Dispatchers.IO) {
            songDao?.update(song)
        }
    }

    suspend fun getAllSongs(): List<Song> {
        var allSongs: List<Song>
        withContext(Dispatchers.IO) {
            allSongs = songDao?.getAll() ?: emptyList()
        }
        return allSongs
    }

}