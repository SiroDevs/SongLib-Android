package com.songlib.feature.settings.viewmodel

import android.util.Log
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.songlib.core.data.repos.ListingRepo
import com.songlib.core.data.repos.PrefsRepo
import com.songlib.core.data.repos.SongBookRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val prefsRepo: PrefsRepo,
    private val songbkRepo: SongBookRepo,
    private val listRepo: ListingRepo,
) : ViewModel() {

    var horizontalSlides by mutableStateOf(prefsRepo.horizontalSlides)
        private set

    var demoMode by mutableStateOf(prefsRepo.demoMode)
        private set

    var autoPlayEnabled by mutableStateOf(prefsRepo.autoPlayEnabled)
        private set

    fun updateHorizontalSlides(enabled: Boolean) {
        horizontalSlides = enabled
        prefsRepo.horizontalSlides = enabled
    }

    fun updateDemoMode(enabled: Boolean) {
        demoMode = enabled
        prefsRepo.demoMode = enabled
    }

    fun updateAutoPlayEnabled(enabled: Boolean) {
        autoPlayEnabled = enabled
        prefsRepo.autoPlayEnabled = enabled
    }

    fun updateSelection(enabled: Boolean) {
        prefsRepo.initialBooks = prefsRepo.selectedBooks
        prefsRepo.selectAfresh = enabled
    }

    fun clearData(onComplete: (Boolean) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                songbkRepo.deleteAllData()
                listRepo.deleteAllListings()
                withContext(Dispatchers.Main) {
                    prefsRepo.resetAppData()
                }
                onComplete(true)
            } catch (e: Exception) {
                Log.e("SettingsViewModel", "Error clearing data", e)
                onComplete(false)
            }
        }
    }
}