package com.songlib.feature.song.presentor.viewmodel.controller

import com.songlib.core.data.repos.PrefsRepo
import com.songlib.core.data.repos.ReportRepo
import com.songlib.core.database.model.SongEntity
import com.songlib.core.network.dtos.SongReportRequest
import com.songlib.feature.song.presentor.utils.ReportUiState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/** Owns submitting and tracking the state of a "report this song" request. */
class ReportController(
    private val reportRepo: ReportRepo,
    private val prefsRepo: PrefsRepo,
    private val scope: CoroutineScope,
    private val toastEvent: MutableSharedFlow<String>,
) {
    private val _reportState = MutableStateFlow<ReportUiState>(ReportUiState.Idle)
    val reportState: StateFlow<ReportUiState> = _reportState.asStateFlow()

    fun submitReport(
        song: SongEntity,
        bookId: Int,
        reportType: String,
        description: String,
    ) {
        scope.launch {
            _reportState.value = ReportUiState.Submitting
            try {
                reportRepo.submitReport(
                    SongReportRequest(
                        songId = song.songId,
                        bookId = bookId,
                        songNo = song.songNo,
                        songTitle = song.title,
                        reportType = reportType,
                        description = description,
                        reportedBy = prefsRepo.loggedInEmail.takeIf { it.isNotEmpty() }
                    )
                )
                _reportState.value = ReportUiState.Success
                toastEvent.emit("Report submitted — thank you! ✅")
            } catch (e: Exception) {
                _reportState.value = ReportUiState.Error(e.message ?: "Failed to submit report")
                toastEvent.emit("Failed to submit report. Please try again.")
            }
        }
    }

    fun resetReportState() {
        _reportState.value = ReportUiState.Idle
    }
}
