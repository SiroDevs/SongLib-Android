package com.songlib.presentation.viewmodels

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import com.songlib.domain.repository.BookRepository
import com.songlib.domain.repository.PrefsRepository
import com.songlib.domain.repository.SongRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val prefsRepo: PrefsRepository,
    private val bookRepo: BookRepository,
    private val songRepo: SongRepository,
) : ViewModel() {

    var horizontalSlides by mutableStateOf(prefsRepo.horizontalSlides)
        private set

    fun updateHorizontalSlides(enabled: Boolean) {
        horizontalSlides = enabled
        prefsRepo.horizontalSlides = enabled
    }
}
