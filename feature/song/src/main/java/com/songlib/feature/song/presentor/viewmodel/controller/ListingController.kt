package com.songlib.feature.song.presentor.viewmodel.controller

import com.songlib.core.data.repos.ListingRepo
import com.songlib.core.database.model.ListingUi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ListingController(
    private val listRepo: ListingRepo,
    private val scope: CoroutineScope,
    private val toastEvent: MutableSharedFlow<String>,
) {
    private val _listings = MutableStateFlow<List<ListingUi>>(emptyList())
    val listings: StateFlow<List<ListingUi>> get() = _listings

    fun loadListings() {
        scope.launch(Dispatchers.IO) { _listings.value = listRepo.fetchListings(0) }
    }

    fun saveListing(title: String) {
        scope.launch(Dispatchers.IO) {
            listRepo.saveListing(0, title, 0)
            _listings.value = listRepo.fetchListings(0)
        }
    }

    fun saveListItem(parent: ListingUi, songId: Int) {
        scope.launch(Dispatchers.IO) {
            listRepo.saveListItem(parent, songId)
            _listings.value = listRepo.fetchListings(0)
            toastEvent.emit("Added to \"${parent.title}\" ✅")
        }
    }

    fun checkAndHandleNewListing(): Boolean = _listings.value.isNotEmpty()
}
