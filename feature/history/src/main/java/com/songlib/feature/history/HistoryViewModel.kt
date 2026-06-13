package com.songlib.feature.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.songlib.core.data.repos.SongBookRepo
import com.songlib.core.data.repos.TrackingRepo
import com.songlib.core.database.model.BookEntity
import com.songlib.core.database.model.SearchEntity
import com.songlib.core.database.model.SongEntity
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

    private val _views = MutableStateFlow<List<SongEntity>>(emptyList())
    val views: StateFlow<List<SongEntity>> = _views.asStateFlow()

    private val _searches = MutableStateFlow<List<SearchEntity>>(emptyList())
    val searches: StateFlow<List<SearchEntity>> = _searches.asStateFlow()

    /** Map songId → BookEntity so history items can show the songbook title */
    private val _bookMap = MutableStateFlow<Map<Int, BookEntity>>(emptyMap())
    val bookMap: StateFlow<Map<Int, BookEntity>> = _bookMap.asStateFlow()

    /**
     * Map songId → history-row id.
     * Used to delete the correct history row when the user removes a viewed song.
     */
    private val songIdToHistoryId = mutableMapOf<Int, Int>()

    // ── Selection ─────────────────────────────────────────────────────────
    /** Set of *songId* values currently selected in the Views tab */
    private val _selectedViewIds = MutableStateFlow<Set<Int>>(emptySet())
    val selectedViewIds: StateFlow<Set<Int>> = _selectedViewIds.asStateFlow()

    /** Set of *SearchEntity.id* values currently selected in the Searches tab */
    private val _selectedSearchIds = MutableStateFlow<Set<Int>>(emptySet())
    val selectedSearchIds: StateFlow<Set<Int>> = _selectedSearchIds.asStateFlow()

    // ── Load ──────────────────────────────────────────────────────────────
    fun load() {
        viewModelScope.launch {
            val histories = trackingRepo.fetchHistories()
            val allSongs  = songbkRepo.fetchLocalSongs()
            val allBooks  = songbkRepo.fetchLocalBooks()
            val songMap   = allSongs.associateBy { it.songId }
            val bookById  = allBooks.associateBy { it.bookId }

            // Build the songId → historyRowId map (most recent row wins for each song)
            songIdToHistoryId.clear()
            histories.forEach { h -> songIdToHistoryId[h.song] = h.id }

            _views.value   = histories.mapNotNull { songMap[it.song] }.distinctBy { it.songId }
            _bookMap.value = bookById
            _searches.value = trackingRepo.fetchSearches()
        }
    }

    // ── Views tab ─────────────────────────────────────────────────────────
    fun toggleViewSelection(songId: Int) {
        _selectedViewIds.value = if (songId in _selectedViewIds.value)
            _selectedViewIds.value - songId
        else
            _selectedViewIds.value + songId
    }

    fun clearViewSelection() { _selectedViewIds.value = emptySet() }

    fun deleteSelectedViews() {
        viewModelScope.launch {
            _selectedViewIds.value.forEach { songId ->
                // Delete by the history-row id, not the songId
                val historyId = songIdToHistoryId[songId]
                if (historyId != null) trackingRepo.deleteHistoryById(historyId)
            }
            clearViewSelection()
            load()
        }
    }

    // ── Searches tab ──────────────────────────────────────────────────────
    fun toggleSearchSelection(searchId: Int) {
        _selectedSearchIds.value = if (searchId in _selectedSearchIds.value)
            _selectedSearchIds.value - searchId
        else
            _selectedSearchIds.value + searchId
    }

    fun clearSearchSelection() { _selectedSearchIds.value = emptySet() }

    fun deleteSelectedSearches() {
        viewModelScope.launch {
            _selectedSearchIds.value.forEach { trackingRepo.deleteSearchById(it) }
            clearSearchSelection()
            load()
        }
    }

    // ── Clear all ─────────────────────────────────────────────────────────
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
