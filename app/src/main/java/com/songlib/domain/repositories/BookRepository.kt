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
class BookRepository @Inject constructor(
    context: Context,
    private val apiService: ApiService
) {
    private var booksDao: BookDao?

    init {
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
}