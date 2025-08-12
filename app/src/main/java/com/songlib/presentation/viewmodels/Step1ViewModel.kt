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
    private val prefsRepo: PrefsRepository,
    private val bookRepo: BookRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val _books = MutableStateFlow<List<Selectable<Book>>>(emptyList())
    val books: StateFlow<List<Selectable<Book>>> get() = _books

    private fun getSelectedIdsFromPrefs(): Set<Int> =
        prefsRepo.selectedBooks
            .split(",")
            .mapNotNull { it.toIntOrNull() }
            .toSet()

    fun fetchBooks() {
        _uiState.tryEmit(UiState.Loading)
        viewModelScope.launch {
            val selectedIds = getSelectedIdsFromPrefs()

            bookRepo.getBooks()
                .catch { exception ->
                    val errorMessage = when (exception) {
                        is HttpException -> "HTTP Error: ${exception.code()}"
                        else -> "Network error: ${exception.message}"
                    }
                    _uiState.tryEmit(UiState.Error(errorMessage))
                }
                .collect { respData ->
                    _books.emit(
                        respData.map { book ->
                            Selectable(book, isSelected = book.bookId in selectedIds)
                        }
                    )
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
                    val existingIds = getSelectedIdsFromPrefs()
                    val newIds = books.map { it.bookId }.toSet()

                    val booksToInsert = books.filter { it.bookId !in existingIds }
                    val idsToDelete = existingIds - newIds

                    booksToInsert.forEach { bookRepo.saveBook(it) }
                    idsToDelete.forEach { bookRepo.deleteById(it) }

                    prefsRepo.selectedBooks = newIds.joinToString(",")
                } else {
                    books.forEach { bookRepo.saveBook(it) }
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
