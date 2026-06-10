package com.songlib.feature.drafts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.songlib.core.data.repos.DraftRepo
import com.songlib.core.data.repos.PrefsRepo
import com.songlib.core.database.model.DraftEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class DraftsViewModel @Inject constructor(
    private val draftRepo: DraftRepo,
    private val prefsRepo: PrefsRepo,
) : ViewModel() {

    val drafts: StateFlow<List<DraftEntity>> = draftRepo.draftsFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _toastEvent = MutableSharedFlow<String>()
    val toastEvent: SharedFlow<String> = _toastEvent.asSharedFlow()

    fun saveDraft(title: String, content: String, songNo: Int? = null, book: Int? = null) {
        viewModelScope.launch {
            val now = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(Date())
            val draft = DraftEntity(
                title   = title,
                content = content,
                songNo  = songNo,
                book    = book,
                userId  = prefsRepo.loggedInUserId,
                created = now
            )
            draftRepo.saveDraft(draft)
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
