package com.songlib.presentation.settings

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.songlib.domain.repository.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val prefsRepo: PreferencesRepository,
    private val songbkRepo: SongBookRepository,
    private val listRepo: ListingRepository,
) : ViewModel() {

    var horizontalSlides by mutableStateOf(prefsRepo.horizontalSlides)
        private set

    fun updateHorizontalSlides(enabled: Boolean) {
        horizontalSlides = enabled
        prefsRepo.horizontalSlides = enabled
    }

    fun updateSelection(enabled: Boolean) {
        prefsRepo.initialBooks = prefsRepo.selectedBooks
        prefsRepo.selectAfresh = enabled
    }

    fun clearData() {
        viewModelScope.launch {
            songbkRepo.deleteAllData()
            listRepo.deleteAllListings()
            prefsRepo.isDataLoaded = false
            prefsRepo.isDataSelected = false
            prefsRepo.selectedBooks = ""
        }
    }
}