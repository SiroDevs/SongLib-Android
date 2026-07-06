package com.songlib.feature.drafts.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.songlib.core.data.repos.DraftRepo
import com.songlib.core.data.repos.PreferencesRepo
import com.songlib.core.database.model.DraftEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class DraftsViewModel @Inject constructor(
    private val draftRepo: DraftRepo,
    private val prefsRepo: PreferencesRepo,
) : ViewModel() {

    val drafts: StateFlow<List<DraftEntity>> = draftRepo.draftsFlow()
        .stateIn(viewModelScope, SharingStarted.Companion.WhileSubscribed(5000), emptyList())

    private val _toastEvent = MutableSharedFlow<String>()
    val toastEvent: SharedFlow<String> = _toastEvent.asSharedFlow()

    private val _selectedIds = MutableStateFlow<Set<Int>>(emptySet())
    val selectedIds: StateFlow<Set<Int>> = _selectedIds.asStateFlow()

    fun toggleSelection(id: Int) {
        _selectedIds.value = if (_selectedIds.value.contains(id))
            _selectedIds.value - id
        else
            _selectedIds.value + id
    }

    fun clearSelection() { _selectedIds.value = emptySet() }

    fun deleteSelected() {
        viewModelScope.launch {
            _selectedIds.value.forEach { draftRepo.deleteDraft(it) }
            clearSelection()
            _toastEvent.emit("Drafts deleted")
        }
    }

    fun saveDraft(title: String, content: String, songNo: Int? = null, book: Int? = null) {
        viewModelScope.launch {
            val now = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(Date())
            draftRepo.saveDraft(
                DraftEntity(
                    title = title,
                    content = content,
                    songNo = songNo,
                    book = book,
                    userId = prefsRepo.loggedInUserId,
                    created = now
                )
            )
            _toastEvent.emit("Draft saved ✅")
        }
    }

    fun deleteDraft(id: Int) {
        viewModelScope.launch {
            draftRepo.deleteDraft(id)
            _toastEvent.emit("Draft deleted")
        }
    }
}