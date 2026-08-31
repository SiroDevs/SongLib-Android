package com.songlib.feature.song.presentor.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.songlib.core.casting.data.CastingRepo
import com.songlib.core.common.entity.UiState
import com.songlib.core.common.utils.AppFonts
import com.songlib.core.data.repos.AutoPlayRepo
import com.songlib.core.data.repos.DraftRepo
import com.songlib.core.data.repos.ListingRepo
import com.songlib.core.data.repos.PrefsRepo
import com.songlib.core.data.repos.ReportRepo
import com.songlib.core.data.repos.SongBookRepo
import com.songlib.core.data.repos.TrackingRepo
import com.songlib.core.database.model.ListingUi
import com.songlib.core.database.model.SongEntity
import com.songlib.feature.song.presentor.viewmodel.controller.AutoplayController
import com.songlib.feature.song.presentor.viewmodel.controller.DraftController
import com.songlib.feature.song.presentor.viewmodel.controller.ListingController
import com.songlib.feature.song.presentor.viewmodel.controller.ReportController
import com.songlib.feature.song.presentor.viewmodel.controller.SongController
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class PresenterViewModel @Inject constructor(
    songbkRepo: SongBookRepo,
    listRepo: ListingRepo,
    private val prefsRepo: PrefsRepo,
    reportRepo: ReportRepo,
    trackingRepo: TrackingRepo,
    draftRepo: DraftRepo,
    private val castingRepo: CastingRepo,
    autoPlayRepo: AutoPlayRepo,
) : ViewModel() {

    private val _toastEvent = MutableSharedFlow<String>()
    val toastEvent: SharedFlow<String> = _toastEvent.asSharedFlow()

    private val content = SongController(songbkRepo, trackingRepo, castingRepo, viewModelScope)
    private val autoPlay = AutoplayController(
        autoPlayRepo, castingRepo, content, viewModelScope, _toastEvent, prefsRepo.autoPlayEnabled,
    )
    private val listingController = ListingController(listRepo, viewModelScope, _toastEvent)
    private val drafts = DraftController(draftRepo, prefsRepo, viewModelScope, _toastEvent)
    private val report = ReportController(reportRepo, prefsRepo, viewModelScope, _toastEvent)

    val horizontalSlides = prefsRepo.horizontalSlides
    val demoMode = prefsRepo.demoMode

    val uiState: StateFlow<UiState> = content.uiState
    val title: StateFlow<String> = content.title
    val indicators: StateFlow<List<String>> = content.indicators
    val verses: StateFlow<List<String>> = content.verses
    val currentSong: StateFlow<SongEntity?> = content.currentSong
    val isLiked: StateFlow<Boolean> = content.isLiked
    val bookSongs: StateFlow<List<SongEntity>> = content.bookSongs
    val currentSongIndex: StateFlow<Int> = content.currentSongIndex
    val hasPreviousSong: StateFlow<Boolean> = content.hasPreviousSong
    val hasNextSong: StateFlow<Boolean> = content.hasNextSong

    fun loadSong(song: SongEntity, bookTitle: String? = null) {
        autoPlay.resetForNewSong()
        content.loadSong(song, bookTitle)
        autoPlay.loadDurations(song.songId)
        listingController.loadListings()
    }

    fun navigateToNext() {
        content.nextSong()?.let { switchTo(it) }
    }

    fun navigateToPrevious() {
        content.previousSong()?.let { switchTo(it) }
    }

    private fun switchTo(song: SongEntity) {
        autoPlay.resetForNewSong()
        content.navigateToSong(song)
        autoPlay.loadDurations(song.songId)
    }

    fun likeSong(song: SongEntity) = content.likeSong(song)

    val isAutoPlaying: StateFlow<Boolean> = autoPlay.isAutoPlaying
    val autoAdvanceTo: SharedFlow<Int> = autoPlay.autoAdvanceTo

    fun toggleAutoPlay() = autoPlay.toggleAutoPlay()
    fun onVerseIndexChanged(index: Int) = autoPlay.onVerseIndexChanged(index)

    private val _fontSize = MutableStateFlow(AppFonts.DEFAULT_FONT_SP)
    val fontSize: StateFlow<Float> = _fontSize.asStateFlow()
    fun updateFontSize(newSp: Float) {
        _fontSize.value = newSp.coerceIn(AppFonts.MIN_FONT_SP, AppFonts.MAX_FONT_SP)
    }

    val listings: StateFlow<List<ListingUi>> = listingController.listings
    fun saveListing(title: String) = listingController.saveListing(title)
    fun saveListItem(parent: ListingUi, songId: Int) = listingController.saveListItem(parent, songId)
    fun checkAndHandleNewListing(): Boolean = listingController.checkAndHandleNewListing()

    fun copyToDrafts(song: SongEntity) = drafts.copyToDrafts(song)

    val reportState: StateFlow<ReportUiState> = report.reportState
    fun submitReport(song: SongEntity, bookId: Int, reportType: String, description: String) =
        report.submitReport(song, bookId, reportType, description)
    fun resetReportState() = report.resetReportState()

    override fun onCleared() {
        autoPlay.cancel()
        castingRepo.publishIdle()
        super.onCleared()
    }
}
