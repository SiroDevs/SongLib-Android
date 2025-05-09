package com.songlib.presentation.viewmodels

import android.util.Log
import androidx.lifecycle.*
import com.songlib.data.models.*
import com.songlib.domain.entities.UiState
import com.songlib.domain.repositories.SelectionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.*
import retrofit2.HttpException
import javax.inject.Inject

@HiltViewModel
class SelectionViewModel @Inject constructor(
    private val selectionRepo: SelectionRepository,
) : ViewModel() {
    private val _uiState: MutableStateFlow<UiState> = MutableStateFlow(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val _books = MutableStateFlow<List<Book>>(emptyList())
    val books: StateFlow<List<Book>> get() = _books

    private val _songs = MutableStateFlow<List<Song>>(emptyList())
    val songs: StateFlow<List<Song>> get() = _songs

    fun fetchBooks() {
        _uiState.tryEmit(UiState.Loading)

        viewModelScope.launch {
            selectionRepo.getBooks().catch { exception ->
                Log.d("TAG", "fetchData: $exception")
                val errorCode = (exception as? HttpException)?.code()

                val errorMessage = if (errorCode in 400..499) {
                    "Error! Force Refresh"
                } else {
                    "We have some issues connecting to the server: $exception"
                }
                _uiState.tryEmit(UiState.Error(errorMessage))
            }.collect { respData ->
                _books.emit(respData)
            }
            _uiState.tryEmit(UiState.Loaded)
        }
    }


    fun fetchSongs(booksId: String) {
        _uiState.tryEmit(UiState.Loading)

        viewModelScope.launch {
            selectionRepo.getSongsByBook(booksId = booksId).catch { exception ->
                Log.d("TAG", "fetchData: $exception")
                val errorCode = (exception as? HttpException)?.code()

                val errorMessage = if (errorCode in 400..499) {
                    "Error! Force Refresh"
                } else {
                    "We have some issues connecting to the server: $exception"
                }
                _uiState.tryEmit(UiState.Error(errorMessage))
            }.collect { respData ->
                _songs.emit(respData)
            }
            _uiState.tryEmit(UiState.Loaded)
        }
    }


}
