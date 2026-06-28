package com.songlib.feature.drafts.present.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.songlib.core.casting.CastingRepo
import com.songlib.core.common.entity.UiState
import com.songlib.core.common.utils.getSongVerses
import com.songlib.core.data.repos.DraftRepo
import com.songlib.core.database.model.DraftEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DraftPresenterViewModel @Inject constructor(
    private val draftRepo: DraftRepo,
    private val broadcastRepo: CastingRepo,
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val _allDrafts = MutableStateFlow<List<DraftEntity>>(emptyList())

    private val _currentDraft = MutableStateFlow<DraftEntity?>(null)
    val currentDraft: StateFlow<DraftEntity?> = _currentDraft.asStateFlow()

    private val _currentIndex = MutableStateFlow(-1)

    private val _verses = MutableStateFlow<List<String>>(emptyList())
    val verses: StateFlow<List<String>> = _verses.asStateFlow()

    private val _indicators = MutableStateFlow<List<String>>(emptyList())
    val indicators: StateFlow<List<String>> = _indicators.asStateFlow()

    private val _hasPrevious = MutableStateFlow(false)
    val hasPrevious: StateFlow<Boolean> = _hasPrevious.asStateFlow()

    private val _hasNext = MutableStateFlow(false)
    val hasNext: StateFlow<Boolean> = _hasNext.asStateFlow()

    private val _toastEvent = MutableSharedFlow<String>()
    val toastEvent: SharedFlow<String> = _toastEvent.asSharedFlow()

    fun load(draft: DraftEntity) {
        viewModelScope.launch {
            val all = draftRepo.getDrafts()
            _allDrafts.value = all
            val idx = all.indexOfFirst { it.id == draft.id }
            _currentIndex.value = if (idx >= 0) idx else 0
            showDraft(all.getOrNull(_currentIndex.value) ?: draft)
        }
    }

    private fun showDraft(draft: DraftEntity) {
        _currentDraft.value = draft
        val verses = getSongVerses(draft.content)
        _verses.value = verses
        val indicators = verses.mapIndexed { i, _ -> (i + 1).toString() }
        _indicators.value = indicators
        val idx = _currentIndex.value
        _hasPrevious.value = idx > 0
        _hasNext.value = idx < _allDrafts.value.size - 1
        _uiState.value = UiState.Loaded

        broadcastRepo.publishSlide(
            source = "draft",
            title = draft.title,
            verses = verses,
            indicators = indicators,
        )
    }

    /** Called on every verse/page navigation while this presenter screen is on top. */
    fun onVerseIndexChanged(index: Int) {
        broadcastRepo.updateIndex(index)
    }

    override fun onCleared() {
        broadcastRepo.publishIdle()
        super.onCleared()
    }

    fun navigateNext() {
        val idx = _currentIndex.value
        val drafts = _allDrafts.value
        if (idx < drafts.size - 1) {
            _currentIndex.value = idx + 1
            showDraft(drafts[idx + 1])
        }
    }

    fun navigatePrevious() {
        val idx = _currentIndex.value
        val drafts = _allDrafts.value
        if (idx > 0) {
            _currentIndex.value = idx - 1
            showDraft(drafts[idx - 1])
        }
    }
}