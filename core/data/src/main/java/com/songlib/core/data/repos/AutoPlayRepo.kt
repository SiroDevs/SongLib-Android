package com.songlib.core.data.repos

import com.songlib.core.common.utils.AutoPlayDefaults
import com.songlib.core.database.daos.AutoPlayDao
import com.songlib.core.database.model.AutoPlayEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AutoPlayRepo @Inject constructor(
    private val autoPlayDao: AutoPlayDao,
) {
    suspend fun getDurations(songId: Int): AutoPlayEntity =
        withContext(Dispatchers.IO) {
            autoPlayDao.getBySongId(songId) ?: AutoPlayEntity(
                songId = songId,
                verseDuration = 0,
                chorusDuration = 0,
            )
        }

    suspend fun saveDurations(entity: AutoPlayEntity) {
        withContext(Dispatchers.IO) { autoPlayDao.upsert(entity) }
    }
}
