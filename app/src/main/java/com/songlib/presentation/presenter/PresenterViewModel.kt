package com.songlib.presentation.presenter

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.songlib.core.utils.getSongVerses
import com.songlib.core.utils.songItemTitle
import com.songlib.data.models.Song
import com.songlib.domain.entity.UiState
import com.songlib.domain.repository.PrefsRepo
import com.songlib.domain.repository.SongBookRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PresenterViewModel @Inject constructor(
    private val songbkRepo: SongBookRepo,
    private val prefsRepo: PrefsRepo,
) : ViewModel() {
    private val _uiState: MutableStateFlow<UiState> = MutableStateFlow(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val _isLiked = MutableStateFlow(false)
    val isLiked: StateFlow<Boolean> get() = _isLiked

    private val _title = MutableStateFlow("Song Presenter")
    val title: StateFlow<String> get() = _title

    private val _indicators = MutableStateFlow<List<String>>(emptyList())
    val indicators: StateFlow<List<String>> get() = _indicators

    private val _verses = MutableStateFlow<List<String>>(emptyList())
    val verses: StateFlow<List<String>> get() = _verses

    val horizontalSlides = prefsRepo.horizontalSlides

    fun loadSong(song: Song) {
        _uiState.value = UiState.Loading
        _isLiked.value = song.liked
        val content = song.content
        val hasChorus = content.contains("CHORUS")

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

    fun likeSong(song: Song) {
        viewModelScope.launch {
            val updatedSong = song.copy(liked = !song.liked)
            songbkRepo.updateSong(updatedSong)
            _isLiked.value = updatedSong.liked
        }
    }

}