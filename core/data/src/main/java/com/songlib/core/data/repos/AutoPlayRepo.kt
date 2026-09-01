package com.songlib.core.data.repos

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
    /** Returns the learned durations for [songId], or null if nothing has been learned yet.
     *  No fabricated defaults — a song with no observed timing simply has none. */
    suspend fun getDurations(songId: Int): AutoPlayEntity? =
        withContext(Dispatchers.IO) { autoPlayDao.getBySongId(songId) }

    suspend fun saveDurations(entity: AutoPlayEntity) {
        withContext(Dispatchers.IO) { autoPlayDao.upsert(entity) }
    }
}
