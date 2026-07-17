package com.songlib.feature.edits.admin.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.songlib.core.data.repos.EditorRepo
import com.songlib.core.network.dtos.EditDto
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface AdminEditsUiState {
    object Loading : AdminEditsUiState
    object Empty : AdminEditsUiState
    data class Loaded(val edits: List<EditDto>) : AdminEditsUiState
    data class Error(val message: String) : AdminEditsUiState
}

@HiltViewModel
class AdminEditsViewModel @Inject constructor(
    private val editorRepo: EditorRepo,
) : ViewModel() {

    private val _uiState = MutableStateFlow<AdminEditsUiState>(AdminEditsUiState.Loading)
    val uiState: StateFlow<AdminEditsUiState> = _uiState.asStateFlow()

    private val _toastEvent = MutableSharedFlow<String>()
    val toastEvent: SharedFlow<String> = _toastEvent.asSharedFlow()

    /** editId -> true=approving false=rejecting, null=idle */
    private val _pendingAction = MutableStateFlow<Map<Int, Boolean>>(emptyMap())
    val pendingAction: StateFlow<Map<Int, Boolean>> = _pendingAction.asStateFlow()

    fun load() {
        _uiState.value = AdminEditsUiState.Loading
        viewModelScope.launch {
            try {
                val edits = editorRepo.fetchPendingEdits()
                _uiState.value = if (edits.isEmpty()) AdminEditsUiState.Empty
                else AdminEditsUiState.Loaded(edits)
            } catch (e: Exception) {
                _uiState.value = AdminEditsUiState.Error(
                    e.message ?: "Failed to load pending edits"
                )
            }
        }
    }

    fun approve(editId: Int) {
        _pendingAction.value = _pendingAction.value + (editId to true)
        viewModelScope.launch {
            try {
                editorRepo.approveEdit(editId)
                _toastEvent.emit("Edit #$editId approved ✅")
                removeFromList(editId)
            } catch (e: Exception) {
                _toastEvent.emit("Failed to approve edit: ${e.message}")
            } finally {
                _pendingAction.value = _pendingAction.value - editId
            }
        }
    }

    fun reject(editId: Int, reason: String?) {
        _pendingAction.value = _pendingAction.value + (editId to false)
        viewModelScope.launch {
            try {
                editorRepo.rejectEdit(editId, reason)
                _toastEvent.emit("Edit #$editId rejected ❌")
                removeFromList(editId)
            } catch (e: Exception) {
                _toastEvent.emit("Failed to reject edit: ${e.message}")
            } finally {
                _pendingAction.value = _pendingAction.value - editId
            }
        }
    }

    private fun removeFromList(editId: Int) {
        val current = _uiState.value
        if (current is AdminEditsUiState.Loaded) {
            val remaining = current.edits.filter { it.editId != editId }
            _uiState.value = if (remaining.isEmpty()) AdminEditsUiState.Empty
            else AdminEditsUiState.Loaded(remaining)
        }
    }
}
