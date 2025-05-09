package com.songlib.domain.entities

sealed class UiState {
    object Loading : UiState()
    object Loaded : UiState()
    class Error(val errorMessage: String) : UiState()
}
