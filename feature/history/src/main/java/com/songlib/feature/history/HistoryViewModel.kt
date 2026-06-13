package com.songlib.feature.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.songlib.core.database.model.BookEntity
import com.songlib.core.database.model.SearchEntity
import com.songlib.core.data.repos.SongBookRepo
import com.songlib.core.data.repos.TrackingRepo
import com.songlib.feature.history.utils.SongView
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val trackingRepo: TrackingRepo,
    private val songbkRepo: SongBookRepo,
) : ViewModel() {
    private val _views = MutableStateFlow<List<SongView>>(emptyList())
    val views: StateFlow<List<SongView>> = _views.asStateFlow()

    private val _searches = MutableStateFlow<List<SearchEntity>>(emptyList())
    val searches: StateFlow<List<SearchEntity>> = _searches.asStateFlow()

    private val _bookMap = MutableStateFlow<Map<Int, BookEntity>>(emptyMap())
    val bookMap: StateFlow<Map<Int, BookEntity>> = _bookMap.asStateFlow()
    private val historyIdToSongId = mutableMapOf<Int, Int>()

    private val _selectedViewIds = MutableStateFlow<Set<Int>>(emptySet())
    val selectedViewIds: StateFlow<Set<Int>> = _selectedViewIds.asStateFlow()

    private val _selectedSearchIds = MutableStateFlow<Set<Int>>(emptySet())
    val selectedSearchIds: StateFlow<Set<Int>> = _selectedSearchIds.asStateFlow()

    private val _showOlderViews = MutableStateFlow(false)
    val showOlderViews: StateFlow<Boolean> = _showOlderViews.asStateFlow()

    private val _showOlderSearches = MutableStateFlow(false)
    val showOlderSearches: StateFlow<Boolean> = _showOlderSearches.asStateFlow()

    fun toggleOlderViews()   { _showOlderViews.value   = !_showOlderViews.value }
    fun toggleOlderSearches() { _showOlderSearches.value = !_showOlderSearches.value }

    fun load() {
        viewModelScope.launch {
            val histories = trackingRepo.fetchHistories()
            val allSongs  = songbkRepo.fetchLocalSongs()
            val allBooks  = songbkRepo.fetchLocalBooks()
            val songMap   = allSongs.associateBy { it.songId }
            val bookById  = allBooks.associateBy { it.bookId }

            historyIdToSongId.clear()
            histories.forEach { h -> historyIdToSongId[h.id] = h.song }

            // Each HistoryEntity row becomes a SongView; keep all rows (don't dedupe)
            // so that re-views show up with their individual timestamps.
            _views.value    = histories.mapNotNull { h ->
                songMap[h.song]?.let { SongView(song = h, entity = it) }
            }
            _bookMap.value  = bookById
            _searches.value = trackingRepo.fetchSearches()
        }
    }

    fun toggleViewSelection(historyId: Int) {
        _selectedViewIds.value = if (historyId in _selectedViewIds.value)
            _selectedViewIds.value - historyId else _selectedViewIds.value + historyId
    }

    fun clearViewSelection() { _selectedViewIds.value = emptySet() }

    fun deleteSelectedViews() {
        viewModelScope.launch {
            _selectedViewIds.value.forEach { trackingRepo.deleteHistoryById(it) }
            clearViewSelection()
            load()
        }
    }

    fun toggleSearchSelection(searchId: Int) {
        _selectedSearchIds.value = if (searchId in _selectedSearchIds.value)
            _selectedSearchIds.value - searchId else _selectedSearchIds.value + searchId
    }

    fun clearSearchSelection() { _selectedSearchIds.value = emptySet() }

    fun deleteSelectedSearches() {
        viewModelScope.launch {
            _selectedSearchIds.value.forEach { trackingRepo.deleteSearchById(it) }
            clearSearchSelection()
            load()
        }
    }

    fun clearViews() {
        viewModelScope.launch {
            clearViewSelection()
            trackingRepo.deleteAllHistories()
            load()
        }
    }

    fun clearSearches() {
        viewModelScope.launch {
            clearSearchSelection()
            trackingRepo.deleteAllSearches()
            load()
        }
    }
}