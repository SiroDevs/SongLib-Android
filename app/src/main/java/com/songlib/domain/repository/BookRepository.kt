package com.songlib.domain.repository

import android.content.*
import com.songlib.data.models.*
import com.songlib.data.sources.local.*
import com.songlib.data.sources.local.daos.BookDao
import com.songlib.data.sources.remote.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
import javax.inject.*

@Singleton
class BookRepository @Inject constructor(
    context: Context,
    private val apiService: ApiService,
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

    suspend fun deleteById(bookId: Int) {
        withContext(Dispatchers.IO) {
            booksDao?.deleteById(bookId)
        }
    }

    suspend fun getAllBooks(): List<Book> {
        var allBooks: List<Book>
        withContext(Dispatchers.IO) {
            allBooks = booksDao?.getAll() ?: emptyList()
        }
        return allBooks
    }

    suspend fun deleteAllBooks() {
        withContext(Dispatchers.IO) {
            booksDao?.deleteAll()
        }
    }
}