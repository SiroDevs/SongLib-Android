package com.songlib.presentation.viewmodels

import android.util.Log
import androidx.lifecycle.*
import com.songlib.data.models.*
import com.songlib.domain.entities.UiState
import com.songlib.domain.repositories.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.*
import retrofit2.HttpException
import javax.inject.Inject

@HiltViewModel
class PresentorViewModel @Inject constructor(
    private val songRepo: SongRepository,
) : ViewModel() {
    private val _uiState: MutableStateFlow<UiState> = MutableStateFlow(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val _books = MutableStateFlow<List<Book>>(emptyList())
    val books: StateFlow<List<Book>> get() = _books

    private val _songs = MutableStateFlow<List<Song>>(emptyList())
    val songs: StateFlow<List<Song>> get() = _songs

    fun loadSong() {
        _uiState.tryEmit(UiState.Loading)

        _uiState.tryEmit(UiState.Loaded)

    }

}
