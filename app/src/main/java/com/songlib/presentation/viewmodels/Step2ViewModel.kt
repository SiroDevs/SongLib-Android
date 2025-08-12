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
class Step2ViewModel @Inject constructor(
    private val prefsRepo: PrefsRepository,
    private val songRepo: SongRepository,
) : ViewModel() {
    private val _uiState: MutableStateFlow<UiState> = MutableStateFlow(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val _progress = MutableStateFlow(0)
    val progress: StateFlow<Int> = _progress.asStateFlow()

    private val _songs = MutableStateFlow<List<Song>>(emptyList())
    val songs: StateFlow<List<Song>> get() = _songs

    var selectAfresh by mutableStateOf(prefsRepo.selectAfresh)
        private set

    var selectedBooks by mutableStateOf(prefsRepo.selectedBooks)
        private set

    fun fetchSongs() {
        _uiState.tryEmit(UiState.Loading)
        viewModelScope.launch {
            val books = prefsRepo.selectedBooks
            songRepo.getSongs(books).catch { exception ->
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
                prefsRepo.isDataLoaded = true
                _uiState.emit(UiState.Saved)
            } catch (e: Exception) {
                prefsRepo.isDataLoaded = false
                Log.e("SaveSongs", "Failed to save songs", e)
                _uiState.emit(UiState.Error("Failed to save songs: ${e.message}"))
            }
        }
    }
}
