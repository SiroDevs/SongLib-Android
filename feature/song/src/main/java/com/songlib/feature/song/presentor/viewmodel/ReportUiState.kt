package com.songlib.feature.song.presentor.viewmodel

sealed interface ReportUiState {
    object Idle : ReportUiState
    object Submitting : ReportUiState
    object Success : ReportUiState
    data class Error(val message: String) : ReportUiState
}
