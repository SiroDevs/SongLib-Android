package com.songlib.presentation.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.songlib.core.utils.SongUtils
import com.songlib.data.models.Book
import com.songlib.data.models.ListingUi
import com.songlib.data.models.Song
import com.songlib.domain.entity.UiState
import com.songlib.domain.repos.ListingRepo
import com.songlib.domain.repos.PrefsRepo
import com.songlib.domain.repos.SongBookRepo
import com.songlib.presentation.home.components.HomeNavItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val prefsRepo: PrefsRepo,
    private val songbkRepo: SongBookRepo,
    private val listRepo: ListingRepo,
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

    private val _listings = MutableStateFlow<List<ListingUi>>(emptyList())
    val listings: StateFlow<List<ListingUi>> get() = _listings

    private val _isProUser = MutableStateFlow(false)
    val isProUser: StateFlow<Boolean> = _isProUser.asStateFlow()

    private val _showProLimitDialog = MutableStateFlow(false)
    val showProLimitDialog: StateFlow<Boolean> = _showProLimitDialog.asStateFlow()

    fun setSelectedTab(tab: HomeNavItem) {
        _selectedTab.value = tab
    }

    fun fetchData() {
        _uiState.tryEmit(UiState.Loading)
        viewModelScope.launch {
            _isProUser.value = prefsRepo.isProUser
            _books.value = songbkRepo.fetchLocalBooks()
            _songs.value = songbkRepo.fetchLocalSongs()
            _listings.value = listRepo.fetchListings(0)

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

    fun saveListing(title: String) {
        viewModelScope.launch(Dispatchers.IO) {
            listRepo.saveListing(0, title, 0)
            _listings.value = listRepo.fetchListings(0)
            _uiState.tryEmit(UiState.Filtered)
        }
    }

    fun saveListItem(parent: ListingUi, song: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            listRepo.saveListItem(parent, song)
            _listings.value = listRepo.fetchListings(0)
            _uiState.tryEmit(UiState.Filtered)
        }
    }

    fun saveListItems(parent: ListingUi, listings: Set<Song>) {
        _uiState.tryEmit(UiState.Saving)

        viewModelScope.launch(Dispatchers.IO) {
            listings.forEach { saveListItem(parent, it.songId) }
            _uiState.emit(UiState.Filtered)
        }
    }

    fun deleteListings(listings: Set<ListingUi>) {
        _uiState.tryEmit(UiState.Saving)

        viewModelScope.launch(Dispatchers.IO) {
            listings.forEach { listRepo.deleteById(it.id) }
            _uiState.emit(UiState.Filtered)
        }
    }

    fun checkAndHandleNewListing(): Boolean {
        return !_isProUser.value && listings.value.isNotEmpty()
    }

    fun onProLimitProceed() {
        _showProLimitDialog.value = false
    }

    fun onProLimitDismiss() {
        _showProLimitDialog.value = false
    }

    fun clearData(onComplete: (Boolean) -> Unit) {
        _uiState.tryEmit(UiState.Loading)

        viewModelScope.launch(Dispatchers.IO) {
            try {
                songbkRepo.deleteAllData()
                listRepo.deleteAllListings()

                withContext(Dispatchers.Main) {
                    prefsRepo.isDataLoaded = false
                    prefsRepo.isDataSelected = false
                    prefsRepo.selectAfresh = false
                    prefsRepo.initialBooks = ""
                    prefsRepo.selectedBooks = ""
                }

                withContext(Dispatchers.Main) {
                    _books.value = emptyList()
                    _songs.value = emptyList()
                    _filtered.value = emptyList()
                    _likes.value = emptyList()
                    _listings.value = emptyList()
                    _uiState.tryEmit(UiState.Loaded)
                }

                onComplete(true)
            } catch (e: Exception) {
                _uiState.tryEmit(UiState.Error("Error clearing data"))
                Log.e("HomeViewModel", "Error clearing data", e)
                onComplete(false)
            }
        }
    }

}