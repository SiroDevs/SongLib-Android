package com.songlib.presentation.viewmodels

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
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
    private val _uiState: MutableStateFlow<UiState> = MutableStateFlow(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val _books = MutableStateFlow<List<Selectable<Book>>>(emptyList())
    val books: StateFlow<List<Selectable<Book>>> get() = _books

    var selectAfresh by mutableStateOf(prefsRepo.selectAfresh)
        private set

    var selectedBooks by mutableStateOf(prefsRepo.selectedBooks)
        private set

    fun fetchBooks() {
        _uiState.tryEmit(UiState.Loading)
        viewModelScope.launch {
            bookRepo.getBooks().catch { exception ->
                Log.d("TAG", "fetching books")
                val errorMessage = when (exception) {
                    is HttpException -> "HTTP Error: ${exception.code()}"
                    else -> "Network error: ${exception.message}"
                }
                Log.d("TAG", errorMessage)
                _uiState.tryEmit(UiState.Error(errorMessage))
            }.collect { respData ->
                val selectableBooks = respData.map { Selectable(it) }
                _books.emit(selectableBooks)
                _uiState.tryEmit(UiState.Loaded)
            }
        }
    }

    fun getSelectedBooks(): List<Book> {
        return _books.value.filter { it.isSelected }.map { it.data }
    }

    fun saveSelectedBooks() {
        val selected = _books.value.filter { it.isSelected }.map { it.data }
        saveBooks(selected)
    }

    private fun saveBooks(books: List<Book>) {
        _uiState.tryEmit(UiState.Saving)

        viewModelScope.launch(Dispatchers.IO) {
            try {
                for (book in books) {
                    bookRepo.saveBook(book)
                }
                prefsRepo.selectedBooks = books.joinToString(",") { it.bookId.toString() }
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
