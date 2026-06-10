package com.songlib.core.data.repos

import com.songlib.core.database.daos.EditDao
import com.songlib.core.database.model.EditEntity
import com.songlib.core.network.dtos.EditDto
import com.songlib.core.network.services.SongLibService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EditRepo @Inject constructor(
    private val editDao: EditDao,
    private val service: SongLibService
) {
    suspend fun getEditsForUser(userId: Int): List<EditEntity> = editDao.getForUser(userId)

    suspend fun hasEdits(userId: Int): Boolean = editDao.countForUser(userId) > 0

    suspend fun saveEdit(edit: EditEntity): Long = editDao.insert(edit)

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
            val remoteEdits = service.getEdits()
            val myRemote = remoteEdits.filter { it.userId == userId }
            val local = editDao.getForUser(userId)
            myRemote.forEach { remote ->
                val localEdit = local.firstOrNull { it.editId == remote.editId }
                localEdit?.let { editDao.update(it.copy(status = remote.status)) }
            }
        } catch (_: Exception) {}
    }
}
