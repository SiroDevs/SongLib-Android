package com.songlib.domain.repository

import android.content.*
import com.songlib.data.models.*
import com.songlib.data.sources.local.*
import com.songlib.data.sources.local.daos.BookDao
import com.songlib.data.sources.remote.ApiService
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import javax.inject.*

@Singleton
class BookRepository @Inject constructor(
    context: Context,
    private val apiService: ApiService,
) {
    private var bookDao: BookDao?

    init {
        val db = AppDatabase.getDatabase(context)
        bookDao = db?.bookDao()
    }

    fun getBooks(): Flow<List<Book>> = flow {
        val books = apiService.getBooks()
        emit(books)
    }

    suspend fun saveBook(book: Book) {
        withContext(Dispatchers.IO) {
            bookDao?.insert(book)
        }
    }

    suspend fun deleteById(bookId: Int) {
        withContext(Dispatchers.IO) {
            bookDao?.deleteById(bookId)
        }
    }

    suspend fun getAllBooks(): List<Book> {
        var allBooks: List<Book>
        withContext(Dispatchers.IO) {
            allBooks = bookDao?.getAll() ?: emptyList()
        }
        return allBooks
    }

    suspend fun deleteAllBooks() {
        withContext(Dispatchers.IO) {
            bookDao?.deleteAll()
        }
    }
}