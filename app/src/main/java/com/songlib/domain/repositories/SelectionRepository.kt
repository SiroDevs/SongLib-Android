package com.songlib.domain.repositories

import android.content.*
import com.songlib.core.utils.PrefConstants
import com.songlib.data.models.*
import com.songlib.data.sources.remote.ApiService
import kotlinx.coroutines.flow.*
import javax.inject.*

@Singleton
class SelectionRepository @Inject constructor(
    val context: Context,
    private val apiService: ApiService
) : SharedPreferences.OnSharedPreferenceChangeListener {
    private val preferences: SharedPreferences = context.getSharedPreferences(
        PrefConstants.Key.PREFERENCE_FILE,
        Context.MODE_PRIVATE
    )

    //private var activitiesDao: ActivitiesDao?

    private val _widgetStatus = MutableSharedFlow<Boolean>(replay = 0)

    fun refreshPrefs(): Boolean {
        return preferences.getBoolean("widgetEnable", false)
    }

    init {
        //val db = com.siro.mystrava.data.sources.local.AppDatabase.Companion.getDatabase(context)
        //activitiesDao = com.siro.mystrava.data.sources.local.AppDatabase.activitiesDao()

        preferences.registerOnSharedPreferenceChangeListener(this)
    }

    fun getBooks(): Flow<List<Book>> = flow {
        val books: MutableList<Book> = mutableListOf()
        books.addAll(apiService.getBooks().data)
        emit(books)
    }


    fun saveBooks(): Flow<List<Book>> = flow {
        val books: MutableList<Book> = mutableListOf()
        books.addAll(apiService.getBooks().data)
        emit(books)
    }

    fun getSongsByBook(booksId: String): Flow<List<Song>> = flow {
        val songs: MutableList<Song> = mutableListOf()
        songs.addAll(apiService.getSongsByBook(booksId = booksId).data)
        emit(songs)
    }

    override fun onSharedPreferenceChanged(
        sharedPreferences: SharedPreferences?,
        key: String?
    ) {
        TODO("Not yet implemented")
    }

}