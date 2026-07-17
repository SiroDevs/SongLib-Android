package com.songlib.feature.edits.user.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.songlib.core.data.repos.EditorRepo
import com.songlib.core.data.repos.PreferencesRepo
import com.songlib.core.database.model.EditEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditsViewModel @Inject constructor(
    private val editorRepo: EditorRepo,
    private val prefsRepo: PreferencesRepo,
) : ViewModel() {

    private val _edits = MutableStateFlow<List<EditEntity>>(emptyList())
    val edits: StateFlow<List<EditEntity>> = _edits.asStateFlow()

    fun load() {
        viewModelScope.launch {
            val userId = prefsRepo.loggedInUserId
            if (userId > 0) {
                editorRepo.syncEditStatuses(userId)
                _edits.value = editorRepo.getEditsForUser(userId)
            }
        }
    }
}