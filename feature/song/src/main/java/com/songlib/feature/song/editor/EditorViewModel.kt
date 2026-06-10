package com.songlib.feature.song.editor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.songlib.core.data.repos.EditorRepo
import com.songlib.core.data.repos.PrefsRepo
import com.songlib.core.data.repos.SongBookRepo
import com.songlib.core.database.model.SongEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface EditSubmitState {
    object Idle       : EditSubmitState
    object Submitting : EditSubmitState
    object Success    : EditSubmitState
    data class Error(val message: String) : EditSubmitState
}

@HiltViewModel
class EditorViewModel @Inject constructor(
    private val editorRepo: EditorRepo,
    private val songbkRepo: SongBookRepo,
    private val prefsRepo: PrefsRepo,
) : ViewModel() {

    private val _submitState = MutableStateFlow<EditSubmitState>(EditSubmitState.Idle)
    val submitState: StateFlow<EditSubmitState> = _submitState.asStateFlow()

    private val _toastEvent = MutableSharedFlow<String>()
    val toastEvent: SharedFlow<String> = _toastEvent.asSharedFlow()

    /** Title field value — initialised from the song when [initWith] is called. */
    private val _title = MutableStateFlow("")
    val title: StateFlow<String> = _title.asStateFlow()

    /** Content (lyrics) field value. */
    private val _content = MutableStateFlow("")
    val content: StateFlow<String> = _content.asStateFlow()

    private var originalSong: SongEntity? = null

    fun initWith(song: SongEntity) {
        if (originalSong != null) return   // already initialised — don't overwrite on recompose
        originalSong = song
        _title.value = song.title
        _content.value = song.content
    }

    fun onTitleChange(value: String) {
        _title.value = value
    }

    fun onContentChange(value: String) {
        _content.value = value
    }

    /** Save locally and submit to the API. */
    fun submit() {
        val song = originalSong ?: return
        val userId = prefsRepo.loggedInUserId

        if (!prefsRepo.isLoggedIn) {
            viewModelScope.launch {
                _toastEvent.emit("Please sign in to submit song edits.")
            }
            return
        }

        if (_title.value.isBlank()) {
            viewModelScope.launch { _toastEvent.emit("Title cannot be empty.") }
            return
        }

        _submitState.value = EditSubmitState.Submitting

        viewModelScope.launch {
            try {
                // 1. Submit the edit record to the API + persist locally
                editorRepo.submitSongEdit(
                    song         = song,
                    editedTitle  = _title.value.trim(),
                    editedContent = _content.value.trim(),
                    userId       = userId
                )

                // 2. Update the local song copy so it immediately reflects the edit
                val updatedSong = song.copy(
                    title   = _title.value.trim(),
                    content = _content.value.trim()
                )
                songbkRepo.updateSong(updatedSong)

                _submitState.value = EditSubmitState.Success
                _toastEvent.emit("Your edit has been submitted and is awaiting review ✅")
            } catch (e: Exception) {
                _submitState.value = EditSubmitState.Error(
                    e.message ?: "Failed to submit edit"
                )
                _toastEvent.emit("Failed to submit edit. Please try again.")
            }
        }
    }

    fun resetState() {
        _submitState.value = EditSubmitState.Idle
    }
}
