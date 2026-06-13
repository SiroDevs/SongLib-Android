package com.songlib.core.data.repos

import com.songlib.core.database.daos.DraftDao
import com.songlib.core.database.model.DraftEntity
import com.songlib.core.network.dtos.DraftDto
import com.songlib.core.network.services.SongLibService
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DraftRepo @Inject constructor(
    private val draftsDao: DraftDao,
    private val service: SongLibService
) {
    fun draftsFlow(): Flow<List<DraftEntity>> = draftsDao.getAllFlow()

    suspend fun getDrafts(): List<DraftEntity> = draftsDao.getAll()

    suspend fun saveDraft(draft: DraftEntity): Long = draftsDao.insert(draft)

    suspend fun updateDraft(draft: DraftEntity) = draftsDao.update(draft)

    suspend fun deleteDraft(id: Int) = draftsDao.deleteById(id)

    suspend fun syncDraftsToRemote(userId: Int) {
        val unsynced = getDrafts().filter { !it.synced && it.userId == userId }
        unsynced.forEach { draft ->
            try {
                val dto = DraftDto(
                    title   = draft.title,
                    content = draft.content,
                    songNo  = draft.songNo,
                    book    = draft.book,
                    userId  = userId
                )
                val remote = service.createDraft(dto)
                draftsDao.update(draft.copy(draftId = remote.draftId, synced = true))
            } catch (_: Exception) {
                // Will retry on next sync
            }
        }
    }
}
