package com.songlib.presentation.viewmodels

import androidx.lifecycle.*
import com.songlib.core.utils.*
import com.songlib.data.models.*
import com.songlib.domain.entities.UiState
import com.songlib.domain.repositories.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class PresentorViewModel @Inject constructor(
    private val songRepo: SongRepository,
) : ViewModel() {

    private val _uiState: MutableStateFlow<UiState> = MutableStateFlow(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val _title = MutableStateFlow("")
    val title: StateFlow<String> get() = _title

    private val _hasChorus = MutableStateFlow(false)
    val hasChorus: StateFlow<Boolean> get() = _hasChorus

    private val _indicators = MutableStateFlow<List<String>>(emptyList())
    val indicators: StateFlow<List<String>> get() = _indicators

    private val _verses = MutableStateFlow<List<String>>(emptyList())
    val verses: StateFlow<List<String>> get() = _verses

    fun loadSong(song: Song) {
        _uiState.value = UiState.Loading

        val content = song.content
        val hasChorus = content.contains("CHORUS")
        _hasChorus.value = hasChorus

        _title.value = songItemTitle(song.songNo, song.title)

        val songVerses = getSongVerses(content)
        val verseCount = songVerses.size

        val tempIndicators = mutableListOf<String>()
        val tempVerses = mutableListOf<String>()

        if (hasChorus && verseCount > 1) {
            val chorus = songVerses[1].replace("CHORUS#", "")

            tempIndicators.add("1")
            tempIndicators.add("C")
            tempVerses.add(songVerses[0])
            tempVerses.add(chorus)

            for (i in 2 until verseCount) {
                tempIndicators.add(i.toString())
                tempIndicators.add("C")
                tempVerses.add(songVerses[i])
                tempVerses.add(chorus)
            }
        } else {
            for (i in 0 until verseCount) {
                tempIndicators.add((i + 1).toString())
                tempVerses.add(songVerses[i])
            }
        }

        _indicators.value = tempIndicators
        _verses.value = tempVerses

        _uiState.value = UiState.Loaded
    }

}
