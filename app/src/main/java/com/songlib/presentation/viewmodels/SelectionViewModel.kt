package com.songlib.presentation.viewmodels

import android.util.Log
import androidx.lifecycle.*
import com.songlib.data.models.*
import com.songlib.domain.entities.*
import com.songlib.domain.repositories.*
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

    fun saveBooks(books: List<Book>) {
        viewModelScope.launch(Dispatchers.IO) {
            books.forEach {
                bookRepo.saveBook(it)
            }
            _uiState.emit(UiState.Saved)
        }
    }

    fun fetchSongs(books: String) {
        _uiState.tryEmit(UiState.Loading)

        viewModelScope.launch {
            songRepo.getSongs(books = books).catch { exception ->
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

    fun saveSongs(songs: List<Song>) {
        _uiState.tryEmit(UiState.Saving)

        songs.map {
            runBlocking {
                songRepo.saveSong(it)
            }
        }
        _uiState.tryEmit(UiState.Saved)
    }

}
