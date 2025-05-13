package com.songlib.domain.repositories

import android.content.*
import com.songlib.data.models.*
import com.songlib.data.sources.local.*
import com.songlib.data.sources.remote.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
import javax.inject.*

@Singleton
class SongRepository @Inject constructor(
    context: Context,
    private val apiService: ApiService
) {
    private var songsDao: SongDao?

    init {
        val db = AppDatabase.getDatabase(context)
        songsDao = db?.songsDao()
    }

    fun getSongs(books: String): Flow<List<Song>> = flow {
        val songs = apiService.getSongs(books).data
        emit(songs)
    }

    suspend fun saveSong(song: Song) {
        withContext(Dispatchers.IO) {
            songsDao?.insert(song)
        }
    }

}