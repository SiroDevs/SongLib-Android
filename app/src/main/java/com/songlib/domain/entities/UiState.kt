package com.songlib.domain.entities

sealed class UiState {
    object Loading : UiState()
    object Loaded : UiState()
    object Filtered : UiState()
    object Saving : UiState()
    object Saved : UiState()
    class Liked(val status: Boolean) : UiState()
    class Error(val errorMessage: String) : UiState()
}

sealed class ItemState {
    object Liked : ItemState()
    object Unliked : ItemState()
}
