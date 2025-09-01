package com.songlib.presentation.viewmodels

import androidx.lifecycle.*
import com.songlib.core.utils.*
import com.songlib.data.models.*
import com.songlib.domain.entity.*
import com.songlib.domain.repository.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import kotlin.collections.filter
import kotlin.collections.firstOrNull

@HiltViewModel
class ListingViewModel @Inject constructor(
    private val songbkRepo: SongBookRepository,
    private val listRepo: ListingRepository,
    private val prefsRepo: PreferencesRepository,
) : ViewModel() {
    private val _uiState: MutableStateFlow<UiState> = MutableStateFlow(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val _title = MutableStateFlow("Untitled Listing")
    val title: StateFlow<String> get() = _title

    private val _listings = MutableStateFlow<List<ListingUi>>(emptyList())
    val listings: StateFlow<List<ListingUi>> get() = _listings

    fun loadListing(listing: Listing) {
        _uiState.value = UiState.Loading

        viewModelScope.launch {
            _listings.value = listRepo.fetchListings(listing.id)
            _uiState.tryEmit(UiState.Loaded)
        }
    }

    fun saveListing(title: String) {
        viewModelScope.launch(Dispatchers.IO) {
            listRepo.saveListing(0, title, 0)
            _listings.value = listRepo.fetchListings(0)
            _uiState.tryEmit(UiState.Filtered)
        }
    }

    fun saveListItem(parent: Listing, song: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            listRepo.saveListItem(parent, song)
            _listings.value = listRepo.fetchListings(0)
            _uiState.tryEmit(UiState.Filtered)
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
