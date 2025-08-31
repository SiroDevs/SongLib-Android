package com.songlib.presentation.viewmodels

import android.util.Log
import androidx.lifecycle.*
import com.songlib.core.utils.SongUtils
import com.songlib.data.models.*
import com.songlib.domain.entity.UiState
import com.songlib.domain.repository.*
import com.songlib.presentation.screens.home.components.HomeNavItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.*
import javax.inject.Inject
import kotlin.collections.*

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val songbkRepo: SongBookRepository,
    private val listRepo: ListingRepository,
) : ViewModel() {
    private val _uiState: MutableStateFlow<UiState> = MutableStateFlow(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val _selectedBook: MutableStateFlow<Int> = MutableStateFlow<Int>(0)
    val selectedBook: StateFlow<Int> = _selectedBook.asStateFlow()

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

    private val _listings = MutableStateFlow<List<Listing>>(emptyList())
    val listings: StateFlow<List<Listing>> get() = _listings

    fun setSelectedTab(tab: HomeNavItem) {
        _selectedTab.value = tab
    }

    fun fetchData() {
        _uiState.tryEmit(UiState.Loading)
        viewModelScope.launch {
            _books.value = songbkRepo.fetchLocalBooks()
            _songs.value = songbkRepo.fetchLocalSongs()
            _listings.value = listRepo.fetchListings()

            val firstBookId = _books.value.firstOrNull()?.bookId
            _filtered.value = if (firstBookId != null) {
                _songs.value.filter { it.book == firstBookId }
            } else {
                emptyList()
            }
            _likes.value = _songs.value.filter { it.liked }
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
            } else {
                _filtered.value = emptyList()
            }
            _uiState.tryEmit(UiState.Filtered)
        }
    }

    fun searchSongs(qry: String, byNo: Boolean = false) {
        viewModelScope.launch {
            val allSongs = _songs.value
            _filtered.value = SongUtils.searchSongs(allSongs, qry, byNo)
            _uiState.tryEmit(UiState.Filtered)
        }
    }

    fun likeSongs(books: Set<Song>) {
        _uiState.tryEmit(UiState.Saving)

        viewModelScope.launch(Dispatchers.IO) {
            try {
                books.forEach {
                    val likedSong = it.copy(liked = !it.liked)
                    songbkRepo.updateSong(likedSong)
                }
                _uiState.emit(UiState.Filtered)
            } catch (e: Exception) {
                Log.e("Like/Unlike", "Failed to like songs", e)
            }
        }
    }

    fun saveListing(listing: Listing) {
        viewModelScope.launch(Dispatchers.IO) {
            listRepo.saveListing(listing)
        }
    }

    fun deleteListings(listings: Set<Listing>) {
        _uiState.tryEmit(UiState.Saving)

        viewModelScope.launch(Dispatchers.IO) {
            listings.forEach { listRepo.deleteById(it.id) }
            _uiState.emit(UiState.Filtered)
        }
    }

}
