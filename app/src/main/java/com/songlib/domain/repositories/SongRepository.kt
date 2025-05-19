package com.songlib.domain.repositories

import android.content.*
import com.songlib.core.utils.PrefConstants
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
) : SharedPreferences.OnSharedPreferenceChangeListener {
    private val prefs: SharedPreferences = context.getSharedPreferences(
        PrefConstants.PREFERENCE_FILE,
        Context.MODE_PRIVATE
    )
    private var songsDao: SongDao?

    init {
        prefs.registerOnSharedPreferenceChangeListener(this)
        val db = AppDatabase.getDatabase(context)
        songsDao = db?.songsDao()
    }

    fun getSongs(books: String): Flow<List<Song>> = flow {
        val songs = apiService.getSongs(books)
        emit(songs)
    }

    suspend fun saveSong(song: Song) {
        withContext(Dispatchers.IO) {
            songsDao?.insert(song)
        }
    }

    fun getSelectedBookids(): String? {
        return prefs.getString(PrefConstants.SELECTED_BOOKS, "")
    }


    override fun onSharedPreferenceChanged(preferences: SharedPreferences?, key: String?) {
        TODO("Not yet implemented")
    }

}