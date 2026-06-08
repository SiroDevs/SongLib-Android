package com.songlib.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.songlib.core.data.repos.EditRepo
import com.songlib.core.data.repos.PrefsRepo
import com.songlib.core.database.model.EditEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyEditsViewModel @Inject constructor(
    private val editRepo: EditRepo,
    private val prefsRepo: PrefsRepo,
) : ViewModel() {

    private val _edits = MutableStateFlow<List<EditEntity>>(emptyList())
    val edits: StateFlow<List<EditEntity>> = _edits.asStateFlow()

    fun load() {
        viewModelScope.launch {
            val userId = prefsRepo.loggedInUserId
            if (userId > 0) {
                editRepo.syncEditStatuses(userId)
                _edits.value = editRepo.getEditsForUser(userId)
            }
        }
    }
}
