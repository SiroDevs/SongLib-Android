package com.songlib.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.songlib.core.data.repos.SongBookRepo
import com.songlib.core.data.repos.TrackingRepo
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

    private val _views    = MutableStateFlow<List<SongEntity>>(emptyList())
    val views: StateFlow<List<SongEntity>> = _views.asStateFlow()

    private val _searches = MutableStateFlow<List<SearchEntity>>(emptyList())
    val searches: StateFlow<List<SearchEntity>> = _searches.asStateFlow()

    fun load() {
        viewModelScope.launch {
            val histories = trackingRepo.fetchHistories()
            val allSongs  = songbkRepo.fetchLocalSongs()
            val songMap   = allSongs.associateBy { it.songId }
            _views.value  = histories.mapNotNull { songMap[it.song] }.distinctBy { it.songId }
            _searches.value = trackingRepo.fetchSearches()
        }
    }

    fun clearViews() {
        viewModelScope.launch { trackingRepo.deleteAllHistories(); load() }
    }

    fun clearSearches() {
        viewModelScope.launch { trackingRepo.deleteAllSearches(); load() }
    }
}
