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
class SelectionViewModel @Inject constructor(
    private val bookRepo: BookRepository,
    private val songRepo: SongRepository,
) : ViewModel() {
    private val _uiState: MutableStateFlow<UiState> = MutableStateFlow(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val _progress = MutableStateFlow(0)
    val progress: StateFlow<Int> = _progress.asStateFlow()

    private val _status = MutableStateFlow("Saving songs ...")
    val status: StateFlow<String> = _status.asStateFlow()

    private val _books = MutableStateFlow<List<Selectable<Book>>>(emptyList())
    val books: StateFlow<List<Selectable<Book>>> get() = _books

    private val _songs = MutableStateFlow<List<Song>>(emptyList())
    val songs: StateFlow<List<Song>> get() = _songs

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

    fun saveSelectedBooks() {
        val selected = getSelectedBooks()
        saveBooks(selected)
    }

    private fun saveBooks(books: List<Book>) {
        _uiState.tryEmit(UiState.Saving)

        viewModelScope.launch(Dispatchers.IO) {
            try {
                for (book in books) {
                    bookRepo.saveBook(book)
                }
                val selectedBooks = books.joinToString(",") { it.bookId.toString() }
                bookRepo.savePrefs(selectedBooks)

                _uiState.emit(UiState.Saved)
            } catch (e: Exception) {
                Log.e("SaveBooks", "Failed to save books", e)
                _uiState.emit(UiState.Error("Failed to save books: ${e.message}"))
            }
        }
    }

    fun fetchSongs() {
        _uiState.tryEmit(UiState.Loading)

        viewModelScope.launch {
            val books = songRepo.getSelectedBookIds()
            songRepo.getSongs(books.toString()).catch { exception ->
                Log.d("TAG", "fetching songs")
                val errorMessage = when (exception) {
                    is HttpException -> "HTTP Error: ${exception.code()}"
                    else -> "Network error: ${exception.message}"
                }
                Log.d("TAG", errorMessage)
                _uiState.tryEmit(UiState.Error(errorMessage))
            }.collect { respData ->
                _songs.emit(respData)
            }
            _uiState.tryEmit(UiState.Loaded)
        }
    }

    fun saveSongs() {
        Log.d("TAG", "Saving songs")
        val songs = _songs.value
        val total = songs.size
        _uiState.tryEmit(UiState.Saving)

        viewModelScope.launch(Dispatchers.IO) {
            try {
                songs.forEachIndexed { index, song ->
                    songRepo.saveSong(song)
                    val percent = ((index + 1).toFloat() / total * 100).toInt()
                    _progress.emit(percent)
                }
                songRepo.setDataLoaded(isLoaded = true)
                _uiState.emit(UiState.Saved)
            } catch (e: Exception) {
                songRepo.setDataLoaded(isLoaded = false)
                Log.e("SaveSongs", "Failed to save songs", e)
                _uiState.emit(UiState.Error("Failed to save songs: ${e.message}"))
            }
        }
    }

    fun toggleBookSelection(book: Selectable<Book>) {
        _books.value = _books.value.map {
            if (it.data.bookId == book.data.bookId) it.copy(isSelected = !it.isSelected) else it
        }
    }

    fun getSelectedBooks(): List<Book> {
        return _books.value.filter { it.isSelected }.map { it.data }
    }
}
