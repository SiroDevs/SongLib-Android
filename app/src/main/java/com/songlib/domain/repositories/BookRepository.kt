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
import androidx.core.content.edit

@Singleton
class BookRepository @Inject constructor(
    context: Context,
    private val apiService: ApiService
) : SharedPreferences.OnSharedPreferenceChangeListener {
    private val prefs: SharedPreferences = context.getSharedPreferences(
        PrefConstants.PREFERENCE_FILE,
        Context.MODE_PRIVATE
    )
    private var booksDao: BookDao?

    init {
        prefs.registerOnSharedPreferenceChangeListener(this)
        val db = AppDatabase.getDatabase(context)
        booksDao = db?.booksDao()
    }

    fun getBooks(): Flow<List<Book>> = flow {
        val books = apiService.getBooks()
        emit(books)
    }

    suspend fun saveBook(book: Book) {
        withContext(Dispatchers.IO) {
            booksDao?.insert(book)
        }
    }

    fun savePrefs(selectedBooks: String) {
        prefs.edit { putString(PrefConstants.SELECTED_BOOKS, selectedBooks) }
        prefs.edit { putBoolean(PrefConstants.DATA_SELECTED, true) }
    }

    override fun onSharedPreferenceChanged(preferences: SharedPreferences?, key: String?) {
        TODO("Not yet implemented")
    }
}