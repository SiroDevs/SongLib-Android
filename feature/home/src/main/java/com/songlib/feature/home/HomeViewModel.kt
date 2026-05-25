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
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
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

    private val _selectedBook: MutableStateFlow<Int> = MutableStateFlow<Int>(-1)
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

    private val _toastEvent = MutableSharedFlow<String>()
    val toastEvent: SharedFlow<String> = _toastEvent.asSharedFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _searchByNo = MutableStateFlow(false)

    private var searchJob: Job? = null

    // Unified selection state for top bar
    private val _selectedSongs = MutableStateFlow<Set<SongEntity>>(emptySet())
    val selectedSongs: StateFlow<Set<SongEntity>> = _selectedSongs.asStateFlow()

    private val _selectedListings = MutableStateFlow<Set<ListingUi>>(emptySet())
    val selectedListings: StateFlow<Set<ListingUi>> = _selectedListings.asStateFlow()

    fun toggleSongSelection(song: SongEntity) {
        _selectedSongs.value = if (_selectedSongs.value.contains(song))
            _selectedSongs.value - song else _selectedSongs.value + song
    }

    fun clearSongSelection() { _selectedSongs.value = emptySet() }

    fun toggleListingSelection(listing: ListingUi) {
        _selectedListings.value = if (_selectedListings.value.contains(listing))
            _selectedListings.value - listing else _selectedListings.value + listing
    }

    fun clearListingSelection() { _selectedListings.value = emptySet() }

    fun setSelectedTab(tab: HomeNavItem) {
        _selectedTab.value = tab
    }

    fun fetchData() {
        _uiState.tryEmit(UiState.Loading)
        viewModelScope.launch {
            _books.value = songbkRepo.fetchLocalBooks()
            _songs.value = songbkRepo.fetchLocalSongs()
            _listings.value = listRepo.fetchListings(0)

            _selectedBook.value = -1
            _filtered.value = _songs.value
            _likes.value = _songs.value.filter { it.liked }
            _uiState.tryEmit(UiState.Filtered)
        }
    }

    fun filterSongs(bookIndex: Int) {
        _selectedBook.value = bookIndex
        _searchQuery.value = ""
        _searchByNo.value = false
        refreshData()
    }

    fun refreshData() {
        viewModelScope.launch {
            _filtered.value = songsForCurrentBook()
            _uiState.tryEmit(UiState.Filtered)
        }
    }

    fun searchSongs(qry: String, byNo: Boolean = false) {
        _searchQuery.value = qry
        _searchByNo.value = byNo

        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            if (!byNo) delay(150)

            val pool = songsForCurrentBook()

            _filtered.value = if (qry.isBlank()) pool
            else SongUtils.searchSongs(pool, qry, byNo)
            _uiState.tryEmit(UiState.Filtered)
        }
    }

    private fun songsForCurrentBook(): List<SongEntity> {
        val bookIndex = _selectedBook.value
        val bookList  = _books.value
        val songList  = _songs.value
        return when {
            bookIndex == -1 -> songList
            bookIndex in bookList.indices -> {
                val bookId = bookList[bookIndex].bookId
                songList.filter { it.book == bookId }
            }
            else -> songList
        }
    }

    fun likeSongs(songs: Set<SongEntity>) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val allLiked = songs.all { it.liked }
                songs.forEach { song ->
                    val updated = song.copy(liked = !song.liked)
                    songbkRepo.updateSong(updated)
                }

                val updatedIds = songs.map { it.songId }.toSet()
                val newSongList = _songs.value.map { s ->
                    if (s.songId in updatedIds) s.copy(liked = !s.liked) else s
                }
                withContext(Dispatchers.Main) {
                    _songs.value = newSongList
                    _filtered.value = _filtered.value.map { s ->
                        if (s.songId in updatedIds) s.copy(liked = !s.liked) else s
                    }
                    _likes.value = newSongList.filter { it.liked }
                    _selectedSongs.value = emptySet()
                    _uiState.tryEmit(UiState.Filtered)

                    val msg = if (allLiked) {
                        if (songs.size == 1) "Removed from likes" else "Removed ${songs.size} songs from likes"
                    } else {
                        if (songs.size == 1) "Added to likes ❤️" else "Added ${songs.size} songs to likes ❤️"
                    }
                    _toastEvent.emit(msg)
                }
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
        viewModelScope.launch(Dispatchers.IO) {
            listings.forEach { saveListItem(parent, it.songId) }
            withContext(Dispatchers.Main) {
                _toastEvent.emit("Added ${listings.size} song${if (listings.size == 1) "" else "s"} to \"${parent.title}\"")
            }
            _uiState.emit(UiState.Filtered)
        }
    }

    fun deleteListings(listings: Set<ListingUi>) {
        viewModelScope.launch(Dispatchers.IO) {
            listings.forEach { listRepo.deleteById(it.id) }
            _listings.value = listRepo.fetchListings(0)
            _selectedListings.value = emptySet()
            _uiState.emit(UiState.Filtered)
        }
    }

    fun checkAndHandleNewListing(): Boolean {
        return listings.value.isNotEmpty()
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