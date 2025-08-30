package com.songlib.presentation.viewmodels

import android.util.Log
import androidx.lifecycle.*
import com.songlib.data.models.*
import com.songlib.domain.entity.UiState
import com.songlib.domain.repository.*
import com.songlib.presentation.screens.home.components.HomeNavItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.*
import javax.inject.Inject
import kotlin.collections.filter
import kotlin.collections.firstOrNull

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val bookRepo: BookRepository,
    private val songRepo: SongRepository,
) : ViewModel() {
    private val _uiState: MutableStateFlow<UiState> = MutableStateFlow(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val _selectedBook: MutableStateFlow<Int> = MutableStateFlow<Int>(0)
    val selectedBook: StateFlow<Int> = _selectedBook.asStateFlow()

    private val _selectedSong: MutableStateFlow<Int> = MutableStateFlow<Int>(0)
    val selectedSong: StateFlow<Int> = _selectedSong.asStateFlow()

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
            val booksList = bookRepo.getAllBooks()
            val songsList = songRepo.getAllSongs()
            _books.value = booksList
            _songs.value = songsList

            val firstBookId = booksList.firstOrNull()?.bookId
            _filtered.value = if (firstBookId != null) {
                songsList.filter { it.book == firstBookId }
            } else {
                emptyList()
            }

            _likes.value = songsList.filter { it.liked }
            _uiState.tryEmit(UiState.Filtered)
        }
    }

    fun filterSongs(bookIndex: Int) {
        _selectedBook.value = bookIndex
        refreshData()
    }

    fun refreshData() {
        viewModelScope.launch {
            val bookIndex = _selectedBook.value
            val bookList = _books.value
            val songList = _songs.value

            if (bookIndex in bookList.indices) {
                val selectedBookId = bookList[bookIndex].bookId
                _filtered.value = songList.filter { it.book == selectedBookId }
                Log.d("TAG", "🎵 filtered songs count: ${_filtered.value.size}")
            } else {
                _filtered.value = emptyList()
                Log.d("TAG", "No 🎵 filtered songs were found")
            }

            _uiState.tryEmit(UiState.Filtered)
        }
    }

    fun searchSongs(qry: String, byNo: Boolean = false) {
        viewModelScope.launch {
            val allSongs = _songs.value
            val query = qry.trim()

            if (query.isBlank()) {
                _filtered.value = allSongs
                return@launch
            }

            if (byNo) {
                val number = query.toIntOrNull()
                _filtered.value = if (number != null) {
                    allSongs.filter { it.songNo == number }
                } else {
                    emptyList()
                }
                _uiState.tryEmit(UiState.Filtered)
                return@launch
            }

            val charsPattern = "[!,]".toRegex()
            val words = if (query.contains(',')) {
                query.split(',').map { it.trim() }
            } else {
                listOf(query.lowercase())
            }

            val queryPattern =
                words.joinToString(".*") { Regex.escape(it) }.toRegex(RegexOption.IGNORE_CASE)

            val filteredSongs = allSongs.filter { song ->
                val title = song.title.replace(charsPattern, "").lowercase()
                val alias = song.alias.replace(charsPattern, "").lowercase()
                val content = song.content.replace(charsPattern, "").lowercase()

                queryPattern.containsMatchIn(title)
                        || queryPattern.containsMatchIn(alias)
                        || queryPattern.containsMatchIn(content)
            }

            _filtered.value = filteredSongs
            _uiState.tryEmit(UiState.Filtered)
        }
    }
}
