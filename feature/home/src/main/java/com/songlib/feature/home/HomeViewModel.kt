package com.songlib.feature.home

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.songlib.core.common.utils.SongUtils
import com.songlib.core.data.repos.EditorRepo
import com.songlib.core.data.repos.ListingRepo
import com.songlib.core.data.repos.PreferencesRepo
import com.songlib.core.data.repos.SongBookRepo
import com.songlib.core.data.repos.TrackingRepo
import com.songlib.core.data.worker.SyncWorker
import com.songlib.core.database.model.BookEntity
import com.songlib.core.database.model.ListingUi
import com.songlib.core.database.model.SongEntity
import com.songlib.core.common.entity.UiState
import com.songlib.feature.home.components.HomeTab
import com.songlib.feature.home.utils.HomeUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
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
import kotlinx.coroutines.withTimeoutOrNull
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val songbkRepo: SongBookRepo,
    private val listRepo: ListingRepo,
    private val prefsRepo: PreferencesRepo,
    private val trackingRepo: TrackingRepo,
    private val editorRepo: EditorRepo,
    @ApplicationContext private val context: Context,
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val _demoMode = MutableStateFlow(prefsRepo.demoMode)
    val demoMode: StateFlow<Boolean> = _demoMode.asStateFlow()

    private val _selectedBook = MutableStateFlow(-1)
    val selectedBook: StateFlow<Int> = _selectedBook.asStateFlow()

    private val _selectedTab = MutableStateFlow<HomeTab>(HomeTab.Search)
    val selectedTab: StateFlow<HomeTab> = _selectedTab.asStateFlow()

    private val _books = MutableStateFlow<List<BookEntity>>(emptyList())
    val books: StateFlow<List<BookEntity>> = _books.asStateFlow()

    private val _songs = MutableStateFlow<List<SongEntity>>(emptyList())
    val songs: StateFlow<List<SongEntity>> = _songs.asStateFlow()

    private val _filtered = MutableStateFlow<List<SongEntity>>(emptyList())
    val filtered: StateFlow<List<SongEntity>> = _filtered.asStateFlow()

    private val _likes = MutableStateFlow<List<SongEntity>>(emptyList())
    val likes: StateFlow<List<SongEntity>> = _likes.asStateFlow()

    private val _listings = MutableStateFlow<List<ListingUi>>(emptyList())
    val listings: StateFlow<List<ListingUi>> = _listings.asStateFlow()

    private val _toastEvent = MutableSharedFlow<String>()
    val toastEvent: SharedFlow<String> = _toastEvent.asSharedFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _searchByNo = MutableStateFlow(false)
    private var searchJob: Job? = null

    private val _selectedSongs = MutableStateFlow<Set<SongEntity>>(emptySet())
    val selectedSongs: StateFlow<Set<SongEntity>> = _selectedSongs.asStateFlow()

    private val _selectedListings = MutableStateFlow<Set<ListingUi>>(emptySet())
    val selectedListings: StateFlow<Set<ListingUi>> = _selectedListings.asStateFlow()

    private val _hasHistory = MutableStateFlow(false)
    val hasHistory: StateFlow<Boolean> = _hasHistory.asStateFlow()

    private val _hasEdits = MutableStateFlow(false)
    val hasEdits: StateFlow<Boolean> = _hasEdits.asStateFlow()

    private var dataFetched = false

    private companion object {
        const val SYNC_OBSERVE_TIMEOUT_MS = 20_000L
    }

    fun dismissDemo() {
        prefsRepo.demoMode = false
        _demoMode.value = false
    }

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

    fun setSelectedTab(homeTab: HomeTab) { _selectedTab.value = homeTab }

    fun fetchData() {
        if (dataFetched) return
        dataFetched = true
        viewModelScope.launch {
            _uiState.tryEmit(UiState.Loading)
            loadFromDb()
            // IMPORTANT: react to isDataLoaded alone. Previously this also required
            // _songs.value.isEmpty(), so on devices that already had cached songs but
            // isDataLoaded == false (e.g. flag reset, fresh-DB-but-old-prefs edge cases,
            // or a sync that was scheduled by MainViewModel before this screen existed)
            // the worker was never observed and the UI stayed on UiState.Loading forever,
            // even though loadFromDb() below already forces Loading for that same case.
            if (!prefsRepo.isDataLoaded) {
                observeInstallSyncWorker()
            }
        }
    }

    private suspend fun loadFromDb() {
        _books.value = songbkRepo.fetchLocalBooks()
        _songs.value = songbkRepo.fetchLocalSongs()
        _listings.value = listRepo.fetchListings(0)
        _selectedBook.value = -1
        _filtered.value = _songs.value
        _likes.value = _songs.value.filter { it.liked }
        _hasHistory.value = trackingRepo.fetchHistories().isNotEmpty()

        val userId = prefsRepo.loggedInUserId
        if (userId > 0) _hasEdits.value = editorRepo.hasEdits(userId)

        // Show cached content the moment we have any, regardless of the isDataLoaded
        // flag. isDataLoaded only gates whether we ALSO kick off/observe a background
        // sync (see fetchData()) — it should never be the sole reason we show a
        // skeleton over songs we already have on disk.
        _uiState.tryEmit(if (_songs.value.isNotEmpty()) UiState.Filtered else UiState.Loading)
    }

    private fun observeInstallSyncWorker() {
        viewModelScope.launch(Dispatchers.Main) {
            if (_songs.value.isEmpty()) _uiState.tryEmit(UiState.Loading)
            try {
                val result = withTimeoutOrNull(SYNC_OBSERVE_TIMEOUT_MS) {
                    WorkManager.getInstance(context)
                        .getWorkInfosByTagFlow(SyncWorker.TAG)
                        .collect { workInfoList ->
                            val info = workInfoList.firstOrNull() ?: return@collect
                            when (info.state) {
                                WorkInfo.State.SUCCEEDED -> {
                                    loadFromDb()
                                    return@collect
                                }
                                WorkInfo.State.FAILED,
                                WorkInfo.State.CANCELLED -> {
                                    if (_songs.value.isNotEmpty()) {
                                        _uiState.tryEmit(UiState.Filtered)
                                    } else {
                                        _uiState.tryEmit(UiState.Error("Failed to load data"))
                                    }
                                    return@collect
                                }
                                WorkInfo.State.RUNNING,
                                WorkInfo.State.ENQUEUED -> {
                                    if (_songs.value.isEmpty()) _uiState.tryEmit(UiState.Loading)
                                }
                                else -> { /* other states */ }
                            }
                        }
                }
                // Safety net: on some OEMs (aggressive battery/doze restrictions, or a
                // WorkManager tag that never got enqueued for whatever reason) the flow
                // above can sit with an empty/absent WorkInfo list indefinitely and NEVER
                // emit a terminal state. Without this we'd be stuck on the skeleton forever
                // with no crash and no log to point at. Fall back gracefully instead.
                if (result == null) {
                    Log.w("HomeViewModel", "Timed out waiting for SyncWorker state")
                    if (_songs.value.isNotEmpty()) {
                        _uiState.tryEmit(UiState.Filtered)
                    } else {
                        _uiState.tryEmit(UiState.Error("Failed to load data"))
                    }
                }
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Worker observation error", e)
                if (_songs.value.isNotEmpty()) {
                    _uiState.tryEmit(UiState.Filtered)
                } else {
                    _uiState.tryEmit(UiState.Error("Error loading data"))
                }
            }
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
            _filtered.value = if (qry.isBlank()) pool else SongUtils.searchSongs(pool, qry, byNo)
            _uiState.tryEmit(UiState.Filtered)
        }
    }

    fun commitSearch(qry: String = _searchQuery.value) {
        if (qry.isBlank()) return
        viewModelScope.launch {
            trackingRepo.recordSearch(qry)
            _hasHistory.value = true
        }
    }

    private fun songsForCurrentBook(): List<SongEntity> =
        HomeUtils.filterSongsForBook(_songs.value, _books.value, _selectedBook.value)

    fun likeSongs(songs: Set<SongEntity>) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val allLiked = songs.all { it.liked }
                songs.forEach { songbkRepo.updateSong(it.copy(liked = !it.liked)) }

                val updatedIds = songs.map { it.songId }.toSet()
                val newSongList = HomeUtils.applyLikeToggle(_songs.value, updatedIds)

                withContext(Dispatchers.Main) {
                    _songs.value = newSongList
                    _filtered.value = HomeUtils.applyLikeToggle(_filtered.value, updatedIds)
                    _likes.value = newSongList.filter { it.liked }
                    _selectedSongs.value = emptySet()
                    _uiState.tryEmit(UiState.Filtered)
                    _toastEvent.emit(HomeUtils.buildLikeToastMessage(songs.size, allLiked))
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

    private suspend fun saveListItemSuspend(parent: ListingUi, song: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            saveListItemSuspend(parent, song)
            _listings.value = listRepo.fetchListings(0)
            _uiState.tryEmit(UiState.Filtered)
        }
    }

    fun saveListItems(parent: ListingUi, songs: Set<SongEntity>) {
        viewModelScope.launch(Dispatchers.IO) {
            songs.forEach { saveListItemSuspend(parent, it.songId) }
            _listings.value = listRepo.fetchListings(0)
            withContext(Dispatchers.Main) {
                val noun = if (songs.size == 1) "song" else "${songs.size} songs"
                _toastEvent.emit("Added $noun to \"${parent.title}\"")
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

    fun checkAndHandleNewListing(): Boolean = listings.value.isNotEmpty()

    fun clearData(onComplete: (Boolean) -> Unit) {
        _uiState.tryEmit(UiState.Loading)
        viewModelScope.launch(Dispatchers.IO) {
            try {
                songbkRepo.deleteAllData()
                listRepo.deleteAllListings()
                withContext(Dispatchers.Main) {
                    prefsRepo.resetAppData()
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