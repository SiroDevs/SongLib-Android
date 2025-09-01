package com.songlib.presentation.viewmodels

import android.util.Log
import androidx.lifecycle.*
import com.songlib.data.models.*
import com.songlib.domain.entity.*
import com.songlib.domain.repository.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import kotlin.collections.forEach

@HiltViewModel
class ListingViewModel @Inject constructor(
    private val songbkRepo: SongBookRepository,
    private val listRepo: ListingRepository,
) : ViewModel() {
    private val _uiState: MutableStateFlow<UiState> = MutableStateFlow(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val _listingTitle = MutableStateFlow("Untitled Listing")
    val listingTitle: StateFlow<String> get() = _listingTitle

    private val _listedSongs = MutableStateFlow<List<Song>>(emptyList())
    val listedSongs: StateFlow<List<Song>> get() = _listedSongs

    private val _listings = MutableStateFlow<List<ListingUi>>(emptyList())
    val listings: StateFlow<List<ListingUi>> get() = _listings

    private val _listItems = MutableStateFlow<List<ListingUi>>(emptyList())
    val listItems: StateFlow<List<ListingUi>> get() = _listItems

    fun loadListing(listing: ListingUi) {
        _uiState.value = UiState.Loading

        viewModelScope.launch {
            val listItems = listRepo.fetchListings(listing.id)
            _listingTitle.value = listing.title

            val songs = mutableListOf<Song>()
            listItems.forEach { item ->
                try {
                    songs.add(songbkRepo.fetchSong(item.song))
                } catch (e: Exception) {
                    Log.e("SaveBooks", "⚠️ Missing song ${item.song}", e)
                }
            }

            _listedSongs.value = songs
            _uiState.value = UiState.Loaded
        }
    }

    fun saveListing(title: String) {
        viewModelScope.launch(Dispatchers.IO) {
            listRepo.saveListing(0, title, 0)
//            _listings.value = listRepo.fetchListings(0)
            _uiState.tryEmit(UiState.Filtered)
        }
    }

    fun saveListItem(parent: ListingUi, song: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            listRepo.saveListItem(parent, song)
//            _listings.value = listRepo.fetchListings(0)
            _uiState.tryEmit(UiState.Filtered)
        }
    }

    fun deleteListing(listing: Int) {
        _uiState.tryEmit(UiState.Saving)

        viewModelScope.launch(Dispatchers.IO) {
            listRepo.deleteById(listing)
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

}
