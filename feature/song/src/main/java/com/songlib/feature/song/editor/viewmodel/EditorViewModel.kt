package com.songlib.feature.song.editor.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.songlib.core.data.repos.DraftRepo
import com.songlib.core.data.repos.EditorRepo
import com.songlib.core.data.repos.PreferencesRepo
import com.songlib.core.data.repos.SongBookRepo
import com.songlib.core.database.model.DraftEntity
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
    object Idle : EditSubmitState
    object Submitting : EditSubmitState
    object Success : EditSubmitState
    data class Error(val message: String) : EditSubmitState
}

sealed interface EditorMode {
    data class Song(val entity: SongEntity) : EditorMode
    data class Draft(val entity: DraftEntity) : EditorMode
}

@HiltViewModel
class EditorViewModel @Inject constructor(
    private val editorRepo: EditorRepo,
    private val songbkRepo: SongBookRepo,
    private val draftRepo: DraftRepo,
    private val prefsRepo: PreferencesRepo,
) : ViewModel() {

    private val _submitState = MutableStateFlow<EditSubmitState>(EditSubmitState.Idle)
    val submitState: StateFlow<EditSubmitState> = _submitState.asStateFlow()

    private val _toastEvent = MutableSharedFlow<String>()
    val toastEvent: SharedFlow<String> = _toastEvent.asSharedFlow()

    private val _title = MutableStateFlow("")
    val title: StateFlow<String> = _title.asStateFlow()

    private val _content = MutableStateFlow("")
    val content: StateFlow<String> = _content.asStateFlow()

    private var editorMode: EditorMode? = null

    private fun String.storageToDisplay(): String = replace("#", "\n")

    private fun String.displayToStorage(): String = replace("\n", "#")

    fun initWithSong(song: SongEntity) {
        if (editorMode != null) return
        editorMode = EditorMode.Song(song)
        _title.value = song.title
        // FIX 3: decode # → newline so the TextField shows readable verse breaks
        _content.value = song.content.storageToDisplay()
    }

    fun initWithDraft(draft: DraftEntity) {
        if (editorMode != null) return
        editorMode = EditorMode.Draft(draft)
        _title.value = draft.title
        // FIX 3: same decode for drafts
        _content.value = draft.content.storageToDisplay()
    }

    fun onTitleChange(value: String) {
        _title.value = value
    }

    fun onContentChange(value: String) {
        _content.value = value
    }

    // ── Submit ────────────────────────────────────────────────────────────

    fun submit() {
        if (_title.value.isBlank()) {
            viewModelScope.launch { _toastEvent.emit("Title cannot be empty.") }
            return
        }
        when (val mode = editorMode) {
            is EditorMode.Song -> submitSongEdit(mode.entity)
            is EditorMode.Draft -> submitDraftEdit(mode.entity)
            null -> return
        }
    }

    private fun submitSongEdit(song: SongEntity) {
        if (!prefsRepo.isLoggedIn) {
            viewModelScope.launch { _toastEvent.emit("Please sign in to submit song edits.") }
            return
        }
        _submitState.value = EditSubmitState.Submitting
        viewModelScope.launch {
            try {
                // FIX 3: re-encode newlines → # before saving/submitting
                val storedContent = _content.value.displayToStorage()

                editorRepo.submitSongEdit(
                    song = song,
                    editedTitle = _title.value.trim(),
                    editedContent = storedContent.trim(),
                    userId = prefsRepo.loggedInUserId,
                )
                val updated = song.copy(
                    title = _title.value.trim(),
                    content = storedContent.trim(),
                )
                songbkRepo.updateSong(updated)
                _submitState.value = EditSubmitState.Success
                _toastEvent.emit("Your edit has been submitted and is awaiting review ✅")
            } catch (e: Exception) {
                _submitState.value = EditSubmitState.Error(e.message ?: "Failed to submit edit")
                _toastEvent.emit("Failed to submit edit. Please try again.")
            }
        }
    }

    private fun submitDraftEdit(draft: DraftEntity) {
        _submitState.value = EditSubmitState.Submitting
        viewModelScope.launch {
            try {
                // FIX 3: re-encode newlines → # before saving
                val storedContent = _content.value.displayToStorage()

                val updated = draft.copy(
                    title = _title.value.trim(),
                    content = storedContent.trim(),
                )
                draftRepo.updateDraft(updated)
                _submitState.value = EditSubmitState.Success
                _toastEvent.emit("Draft saved ✅")
            } catch (e: Exception) {
                _submitState.value = EditSubmitState.Error(e.message ?: "Failed to save draft")
                _toastEvent.emit("Failed to save draft. Please try again.")
            }
        }
    }

    fun resetState() {
        _submitState.value = EditSubmitState.Idle
    }
}
