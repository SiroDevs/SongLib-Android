package com.songlib.presentation.viewmodels

import android.util.Log
import androidx.lifecycle.*
import com.songlib.data.models.*
import com.songlib.domain.entity.*
import com.songlib.domain.repository.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.*
import retrofit2.HttpException
import javax.inject.Inject

@HiltViewModel
class Step1ViewModel @Inject constructor(
    private val prefsRepo: PreferencesRepository,
    private val songbkRepo: SongBookRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val _books = MutableStateFlow<List<Selectable<Book>>>(emptyList())
    val books: StateFlow<List<Selectable<Book>>> get() = _books

    private fun getSelectedIds(): Set<Int> =
        prefsRepo.selectedBooks
            .split(",")
            .mapNotNull { it.toIntOrNull() }
            .toSet()

    fun fetchBooks() {
        _uiState.tryEmit(UiState.Loading)

        viewModelScope.launch {
            songbkRepo.fetchRemoteBooks().catch { exception ->
                Log.d("TAG", "fetching books")
                val errorMessage = when (exception) {
                    is HttpException -> "We're sorry. We can't access the songbooks at the moment due to a HTTP Error: ${exception.code()} error on our server. Kindly try again a little later."
                    else -> "We're sorry. We can't access the songbooks at the moment due to a ${exception.message} error on our server. Kindly try again a little later."
                }
                Log.d("TAG", errorMessage)
                _uiState.tryEmit(UiState.Error(errorMessage))
            }.collect { respData ->
                val selectableBooks = respData.map { book->
                    Selectable(book, book.bookId in getSelectedIds())
                }
                _books.emit(selectableBooks)
                _uiState.tryEmit(UiState.Loaded)
            }
        }
    }

    fun getSelectedBookList(): List<Book> {
        return _books.value.filter { it.isSelected }.map { it.data }
    }

    fun saveSelectedBooks() {
        saveBooks(getSelectedBookList())
    }

    private fun saveBooks(books: List<Book>) {
        _uiState.tryEmit(UiState.Saving)

        viewModelScope.launch(Dispatchers.IO) {
            try {
                if (prefsRepo.selectAfresh) {
                    val existingIds = getSelectedIds()
                    val newIds = books.map { it.bookId }.toSet()

                    val booksToInsert = books.filter { it.bookId !in existingIds }
                    val idsToDelete = existingIds - newIds

                    booksToInsert.forEach { songbkRepo.saveBook(it) }
                    idsToDelete.forEach { songbkRepo.deleteById(it) }

                    prefsRepo.selectedBooks = newIds.joinToString(",")
                } else {
                    books.forEach { songbkRepo.saveBook(it) }
                    prefsRepo.selectedBooks = books.joinToString(",") { it.bookId.toString() }
                }

                prefsRepo.isDataSelected = true
                _uiState.emit(UiState.Saved)
            } catch (e: Exception) {
                Log.e("SaveBooks", "Failed to save books", e)
                _uiState.emit(UiState.Error("Failed to save books: ${e.message}"))
            }
        }
    }

    fun toggleBookSelection(book: Selectable<Book>) {
        _books.value = _books.value.map {
            if (it.data.bookId == book.data.bookId) it.copy(isSelected = !it.isSelected) else it
        }
    }
}
