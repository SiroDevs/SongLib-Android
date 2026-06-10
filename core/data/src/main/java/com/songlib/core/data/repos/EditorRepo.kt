package com.songlib.core.data.repos

import com.songlib.core.database.daos.EditDao
import com.songlib.core.database.model.EditEntity
import com.songlib.core.network.dtos.EditDto
import com.songlib.core.network.dtos.EditRejectRequest
import com.songlib.core.network.services.SongLibService
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EditorRepo @Inject constructor(
    private val editDao: EditDao,
    private val service: SongLibService
) {
    private val isoFmt = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US)

    suspend fun getEditsForUser(userId: Int): List<EditEntity> = editDao.getForUser(userId)

    suspend fun hasEdits(userId: Int): Boolean = editDao.countForUser(userId) > 0

    suspend fun saveEdit(edit: EditEntity): Long = editDao.insert(edit)

    /**
     * Submit a new song edit:
     * 1. Persist locally with status=pending so it appears immediately in My Edits.
     * 2. POST to remote and update local record with the server-assigned editId.
     */
    suspend fun submitSongEdit(
        song: com.songlib.core.database.model.SongEntity,
        editedTitle: String,
        editedContent: String,
        userId: Int
    ): EditEntity {
        val now = isoFmt.format(Date())

        // Persist locally first
        val localEdit = EditEntity(
            songId  = song.songId,
            title   = editedTitle,
            content = editedContent,
            userId  = userId,
            status  = "pending",
            created = now,
            synced  = false
        )
        val localId = editDao.insert(localEdit)
        val saved = localEdit.copy(id = localId.toInt())

        // Push to remote
        try {
            val dto = EditDto(
                songId  = song.songId,
                book    = song.book,
                songNo  = song.songNo,
                title   = editedTitle,
                content = editedContent,
                userId  = userId
            )
            val remote = service.createEdit(dto)
            val synced = saved.copy(editId = remote.editId, synced = true)
            editDao.update(synced)
            return synced
        } catch (_: Exception) {
            // Remote failed — leave as unsynced; the daily sync worker will retry
        }

        return saved
    }

    suspend fun syncEditsToRemote(userId: Int) {
        val unsynced = editDao.getForUser(userId).filter { !it.synced }
        unsynced.forEach { edit ->
            try {
                val dto = EditDto(
                    songId  = edit.songId,
                    title   = edit.title,
                    content = edit.content,
                    userId  = userId
                )
                val remote = service.createEdit(dto)
                editDao.update(edit.copy(editId = remote.editId, synced = true))
            } catch (_: Exception) {}
        }
    }

    suspend fun syncEditStatuses(userId: Int) {
        try {
            val remoteEdits = service.getEditsForUser(userId)
            val local = editDao.getForUser(userId)
            remoteEdits.forEach { remote ->
                val localEdit = local.firstOrNull { it.editId == remote.editId }
                localEdit?.let { editDao.update(it.copy(status = remote.status)) }
            }
        } catch (_: Exception) {}
    }

    // ── Admin operations ─────────────────────────────────────────────────

    /** Fetch all pending edits from the server (admin use). */
    suspend fun fetchPendingEdits(): List<EditDto> = service.getPendingEdits()

    /** Admin: approve an edit. */
    suspend fun approveEdit(editId: Int) {
        service.approveEdit(editId)
    }

    /** Admin: reject an edit with an optional reason. */
    suspend fun rejectEdit(editId: Int, reason: String? = null) {
        service.rejectEdit(editId, EditRejectRequest(reason))
    }
}
