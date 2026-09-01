package com.songlib.feature.song.presentor.utils

sealed interface ReportUiState {
    object Idle : ReportUiState
    object Submitting : ReportUiState
    object Success : ReportUiState
    data class Error(val message: String) : ReportUiState
}

data class AutoPlayProgress(
    val elapsedSeconds: Int = 0,
    val totalSeconds: Int = 0,
    /** True while we're timing the first verse of this Auto Play run (badge counts up).
     *  False from the first verse-to-verse transition onward (badge counts down). */
    val isMonitoring: Boolean = true,
)
