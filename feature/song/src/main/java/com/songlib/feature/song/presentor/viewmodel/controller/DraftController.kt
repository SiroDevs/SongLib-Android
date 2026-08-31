package com.songlib.feature.song.presentor.viewmodel.controller

import com.songlib.core.data.repos.DraftRepo
import com.songlib.core.data.repos.PrefsRepo
import com.songlib.core.database.model.DraftEntity
import com.songlib.core.database.model.SongEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/** Owns copying the presented song into the user's drafts for editing. */
class DraftController(
    private val draftRepo: DraftRepo,
    private val prefsRepo: PrefsRepo,
    private val scope: CoroutineScope,
    private val toastEvent: MutableSharedFlow<String>,
) {
    fun copyToDrafts(song: SongEntity) {
        scope.launch {
            val now = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(Date())
            draftRepo.saveDraft(
                DraftEntity(
                    title = song.title,
                    content = song.content,
                    songNo = song.songNo,
                    book = song.book,
                    userId = prefsRepo.loggedInUserId,
                    created = now,
                )
            )
            toastEvent.emit("Copied \"${song.title}\" to Drafts ✅")
        }
    }
}
