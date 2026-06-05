package com.songlib.feature.presenter

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.songlib.core.common.utils.getSongVerses
import com.songlib.core.common.utils.songItemTitle
import com.songlib.core.database.model.SongEntity
import com.songlib.core.common.entity.UiState
import com.songlib.core.data.repos.ListingRepo
import com.songlib.core.data.repos.PrefsRepo
import com.songlib.core.data.repos.SongBookRepo
import com.songlib.core.database.model.ListingUi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class PresenterViewModel @Inject constructor(
    private val songbkRepo: SongBookRepo,
    private val listRepo: ListingRepo,
    private val prefsRepo: PrefsRepo,
) : ViewModel() {
    private val _uiState: MutableStateFlow<UiState> = MutableStateFlow(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val _isLiked = MutableStateFlow(false)
    val isLiked: StateFlow<Boolean> get() = _isLiked

    private val _title = MutableStateFlow("Song Presenter")
    val title: StateFlow<String> get() = _title

    // Separate flow for one-shot user feedback — not tied to uiState
    private val _toastEvent = MutableSharedFlow<String>()
    val toastEvent: SharedFlow<String> = _toastEvent.asSharedFlow()

    private val _indicators = MutableStateFlow<List<String>>(emptyList())
    val indicators: StateFlow<List<String>> get() = _indicators

    private val _verses = MutableStateFlow<List<String>>(emptyList())
    val verses: StateFlow<List<String>> get() = _verses

    val horizontalSlides = prefsRepo.horizontalSlides
    val demoMode = prefsRepo.demoMode

    private val _bookSongs = MutableStateFlow<List<SongEntity>>(emptyList())
    val bookSongs: StateFlow<List<SongEntity>> = _bookSongs.asStateFlow()

    private val _currentSongIndex = MutableStateFlow(-1)
    val currentSongIndex: StateFlow<Int> = _currentSongIndex.asStateFlow()

    private val _listings = MutableStateFlow<List<ListingUi>>(emptyList())
    val listings: StateFlow<List<ListingUi>> get() = _listings

    private val _currentSong = MutableStateFlow<SongEntity?>(null)
    val currentSong: StateFlow<SongEntity?> = _currentSong.asStateFlow()

    fun loadSong(song: SongEntity) {
        _uiState.value = UiState.Loading
        _currentSong.value = song
        _isLiked.value = song.liked
        parseSong(song)

        viewModelScope.launch {
            _listings.value = listRepo.fetchListings(0)

            val allSongs = withContext(Dispatchers.IO) {
                songbkRepo.fetchLocalSongs()
            }
            val siblingsSorted = allSongs
                .filter { it.book == song.book }
                .sortedBy { it.songNo }
            _bookSongs.value = siblingsSorted
            _currentSongIndex.value = siblingsSorted.indexOfFirst { it.songId == song.songId }
        }
    }

    fun navigateToSong(song: SongEntity) {
        _uiState.value = UiState.Loading
        _currentSong.value = song
        _isLiked.value = song.liked
        parseSong(song)
        _currentSongIndex.value = _bookSongs.value.indexOfFirst { it.songId == song.songId }
    }

    fun navigateToNext() {
        val idx = _currentSongIndex.value
        val songs = _bookSongs.value
        if (idx >= 0 && idx < songs.size - 1) navigateToSong(songs[idx + 1])
    }

    fun navigateToPrevious() {
        val idx = _currentSongIndex.value
        val songs = _bookSongs.value
        if (idx > 0) navigateToSong(songs[idx - 1])
    }

    val hasPreviousSong: Boolean
        get() = _currentSongIndex.value > 0

    val hasNextSong: Boolean
        get() = _currentSongIndex.value in 0 until _bookSongs.value.size - 1

    private fun parseSong(song: SongEntity) {
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

    // -------------------------------------------------------------------------
    // Like
    // -------------------------------------------------------------------------

    fun likeSong(song: SongEntity) {
        viewModelScope.launch {
            val updatedSong = song.copy(liked = !song.liked)
            withContext(Dispatchers.IO) { songbkRepo.updateSong(updatedSong) }
            _isLiked.value = updatedSong.liked
            // Also keep _currentSong in sync so the screen reflects the change
            // if it re-reads currentSong elsewhere
            _currentSong.value = updatedSong
        }
    }

    fun saveListing(title: String) {
        viewModelScope.launch(Dispatchers.IO) {
            listRepo.saveListing(0, title, 0)
            _listings.value = listRepo.fetchListings(0)
            // No uiState emit — the song presenter must not re-trigger Loading/Loaded
        }
    }

    fun saveListItem(parent: ListingUi, songId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            listRepo.saveListItem(parent, songId)
            _listings.value = listRepo.fetchListings(0)
            _toastEvent.emit("Added to \"${parent.title}\" ✅")
        }
    }

    fun checkAndHandleNewListing(): Boolean {
        return listings.value.isNotEmpty()
    }
}