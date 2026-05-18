package com.songlib.feature.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.songlib.core.common.utils.SongUtils
import com.songlib.core.database.model.BookEntity
import com.songlib.core.database.model.ListingUi
import com.songlib.core.database.model.SongEntity
import com.songlib.core.common.entity.UiState
import com.songlib.core.data.repos.ListingRepo
import com.songlib.core.data.repos.PrefsRepo
import com.songlib.core.data.repos.SongBookRepo
import com.songlib.feature.home.components.HomeNavItem
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

    private val _books = MutableStateFlow<List<BookEntity>>(emptyList())
    val books: StateFlow<List<BookEntity>> get() = _books

    private val _songs = MutableStateFlow<List<SongEntity>>(emptyList())
    val songs: StateFlow<List<SongEntity>> get() = _songs

    private val _filtered = MutableStateFlow<List<SongEntity>>(emptyList())
    val filtered: StateFlow<List<SongEntity>> get() = _filtered

    private val _likes = MutableStateFlow<List<SongEntity>>(emptyList())
    val likes: StateFlow<List<SongEntity>> get() = _likes

    private val _listings = MutableStateFlow<List<ListingUi>>(emptyList())
    val listings: StateFlow<List<ListingUi>> get() = _listings

    private val _showProLimitDialog = MutableStateFlow(false)
    val showProLimitDialog: StateFlow<Boolean> = _showProLimitDialog.asStateFlow()

    fun setSelectedTab(tab: HomeNavItem) {
        _selectedTab.value = tab
    }

    fun fetchData() {
        _uiState.tryEmit(UiState.Loading)
        viewModelScope.launch {
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

    fun likeSongs(books: Set<SongEntity>) {
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

    fun saveListItems(parent: ListingUi, listings: Set<SongEntity>) {
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
        return listings.value.isNotEmpty()
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