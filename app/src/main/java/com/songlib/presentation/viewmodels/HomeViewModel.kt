package com.songlib.presentation.viewmodels

import androidx.lifecycle.*
import com.songlib.data.models.*
import com.songlib.domain.entities.UiState
import com.songlib.domain.repositories.*
import com.songlib.presentation.screens.home.widgets.HomeNavItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.*
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val bookRepo: BookRepository,
    private val songRepo: SongRepository,
) : ViewModel() {
    private val _uiState: MutableStateFlow<UiState> = MutableStateFlow(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val _selectedTab: MutableStateFlow<HomeNavItem> = MutableStateFlow(HomeNavItem.Search)
    val selectedTab: StateFlow<HomeNavItem> = _selectedTab.asStateFlow()

    private val _books = MutableStateFlow<List<Book>>(emptyList())
    val books: StateFlow<List<Book>> get() = _books

    private val _songs = MutableStateFlow<List<Song>>(emptyList())
    val songs: StateFlow<List<Song>> get() = _songs

    private val _filtered = MutableStateFlow<List<Song>>(emptyList())
    val filtered: StateFlow<List<Song>> get() = _filtered

    private val _likes = MutableStateFlow<List<Song>>(emptyList())
    val likes: StateFlow<List<Song>> get() = _likes

    fun setSelectedTab(tab: HomeNavItem) {
        _selectedTab.value = tab
    }
    fun fetchData() {
        _uiState.tryEmit(UiState.Loading)

        viewModelScope.launch {
            val books = bookRepo.getAllBooks()
            val songs = songRepo.getAllSongs()
            _books.value = books
            _songs.value = songs

            val firstBookId = books.firstOrNull()?.bookId
            _filtered.value = if (firstBookId != null) {
                songs.filter { it.book == firstBookId }
            } else {
                emptyList()
            }

            _likes.value = songs.filter { it.liked }
            _uiState.tryEmit(UiState.Filtered)
        }
    }

    fun filterSongs(bookIndex: Int) {
        viewModelScope.launch {
            /*val selectedBook = books.getOrNull(bookIndex)
            if (selectedBook != null) {
                val bookId = selectedBook.bookId
                _filtered.value = songs.filter { it.book == bookId }
            } else {
                _filtered.value = emptyList()
            }*/
            _uiState.tryEmit(UiState.Filtered)
        }
    }

}
