package com.songlib.feature.song.presentor.viewmodel.controller

import com.songlib.core.casting.data.CastingRepo
import com.songlib.core.common.entity.UiState
import com.songlib.core.common.utils.getSongVerses
import com.songlib.core.common.utils.songItemTitle
import com.songlib.core.data.repos.SongBookRepo
import com.songlib.core.data.repos.TrackingRepo
import com.songlib.core.database.model.SongEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SongController(
    private val songbkRepo: SongBookRepo,
    private val trackingRepo: TrackingRepo,
    private val castingRepo: CastingRepo,
    private val scope: CoroutineScope,
) {
    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val _title = MutableStateFlow("Song Presenter")
    val title: StateFlow<String> get() = _title

    private val _indicators = MutableStateFlow<List<String>>(emptyList())
    val indicators: StateFlow<List<String>> get() = _indicators

    private val _verses = MutableStateFlow<List<String>>(emptyList())
    val verses: StateFlow<List<String>> get() = _verses

    private val _currentSong = MutableStateFlow<SongEntity?>(null)
    val currentSong: StateFlow<SongEntity?> = _currentSong.asStateFlow()

    private val _isLiked = MutableStateFlow(false)
    val isLiked: StateFlow<Boolean> get() = _isLiked

    private val _bookSongs = MutableStateFlow<List<SongEntity>>(emptyList())
    val bookSongs: StateFlow<List<SongEntity>> = _bookSongs.asStateFlow()

    private val _currentSongIndex = MutableStateFlow(-1)
    val currentSongIndex: StateFlow<Int> = _currentSongIndex.asStateFlow()

    private val _hasPreviousSong = MutableStateFlow(false)
    val hasPreviousSong: StateFlow<Boolean> get() = _hasPreviousSong

    private val _hasNextSong = MutableStateFlow(false)
    val hasNextSong: StateFlow<Boolean> get() = _hasNextSong

    private var currentBookTitle: String? = null

    /** Full song load: also (re)fetches the sibling songs in the same book. Used the first
     *  time the presenter opens a song. */
    fun loadSong(song: SongEntity, bookTitle: String? = null) {
        currentBookTitle = bookTitle
        applySong(song)

        scope.launch {
            trackingRepo.recordSongView(song.songId)

            val allSongs = withContext(Dispatchers.IO) { songbkRepo.fetchLocalSongs() }
            val siblingsSorted = allSongs
                .filter { it.book == song.book }
                .sortedBy { it.songNo }
            _bookSongs.value = siblingsSorted
            _currentSongIndex.value = siblingsSorted.indexOfFirst { it.songId == song.songId }
            _hasPreviousSong.value = _currentSongIndex.value > 0
            _hasNextSong.value = _currentSongIndex.value in 0 until _bookSongs.value.size - 1
        }
    }

    /** Lighter song switch for next/previous navigation within an already-loaded book —
     *  reuses the existing sibling list instead of refetching it. */
    fun navigateToSong(song: SongEntity) {
        applySong(song)
        _currentSongIndex.value = _bookSongs.value.indexOfFirst { it.songId == song.songId }
        scope.launch { trackingRepo.recordSongView(song.songId) }
    }

    /** The next song in [bookSongs], or null if there isn't one. */
    fun nextSong(): SongEntity? {
        val idx = _currentSongIndex.value
        val songs = _bookSongs.value
        return if (idx in 0 until songs.size - 1) songs[idx + 1] else null
    }

    /** The previous song in [bookSongs], or null if there isn't one. */
    fun previousSong(): SongEntity? {
        val idx = _currentSongIndex.value
        val songs = _bookSongs.value
        return if (idx > 0) songs[idx - 1] else null
    }

    fun likeSong(song: SongEntity) {
        scope.launch {
            val updatedSong = song.copy(liked = !song.liked)
            withContext(Dispatchers.IO) { songbkRepo.updateSong(updatedSong) }
            _isLiked.value = updatedSong.liked
            _currentSong.value = updatedSong
        }
    }

    private fun applySong(song: SongEntity) {
        _uiState.value = UiState.Loading
        _currentSong.value = song
        _isLiked.value = song.liked
        parseSong(song)
    }

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
            tempIndicators.add("1"); tempIndicators.add("C")
            tempVerses.add(songVerses[0]); tempVerses.add(chorus)
            for (i in 2 until verseCount) {
                tempIndicators.add(i.toString()); tempIndicators.add("C")
                tempVerses.add(songVerses[i]); tempVerses.add(chorus)
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

        castingRepo.publishSlide(
            source = "song",
            title = _title.value,
            book = currentBookTitle,
            verses = tempVerses,
            indicators = tempIndicators,
        )
    }
}
