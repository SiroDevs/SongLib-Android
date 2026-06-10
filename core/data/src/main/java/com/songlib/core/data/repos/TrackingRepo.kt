package com.songlib.core.data.repos

import com.songlib.core.database.daos.HistoryDao
import com.songlib.core.database.daos.SearchDao
import com.songlib.core.database.model.HistoryEntity
import com.songlib.core.database.model.SearchEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TrackingRepo @Inject constructor(
    private var historiesDao: HistoryDao,
    private var searchesDao: SearchDao
) {
    private fun nowString() =
        SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(Date())

    suspend fun recordSongView(songId: Int) {
        withContext(Dispatchers.IO) {
            historiesDao.insert(HistoryEntity(song = songId, created = nowString()))
        }
    }

    suspend fun recordSearch(term: String) {
        if (term.isBlank()) return
        withContext(Dispatchers.IO) {
            searchesDao.upsertSearch(term.trim(), nowString())
        }
    }

    suspend fun saveHistory(history: HistoryEntity) {
        withContext(Dispatchers.IO) { historiesDao.insert(history) }
    }

    suspend fun saveSearch(search: SearchEntity) {
        withContext(Dispatchers.IO) { searchesDao.insert(search) }
    }

    suspend fun fetchHistories(): List<HistoryEntity> {
        return withContext(Dispatchers.IO) { historiesDao.getAll() ?: emptyList() }
    }

    suspend fun fetchSearches(): List<SearchEntity> {
        return withContext(Dispatchers.IO) { searchesDao.getAll() ?: emptyList() }
    }

    suspend fun updateSearch(search: SearchEntity) {
        withContext(Dispatchers.IO) { searchesDao.update(search) }
    }

    suspend fun deleteHistoryById(id: Int) {
        withContext(Dispatchers.IO) { historiesDao.deleteById(id) }
    }

    suspend fun deleteAllHistories() {
        withContext(Dispatchers.IO) { historiesDao.deleteAll() }
    }

    suspend fun deleteSearchById(id: Int) {
        withContext(Dispatchers.IO) { searchesDao.deleteById(id) }
    }

    suspend fun deleteAllSearches() {
        withContext(Dispatchers.IO) { searchesDao.deleteAll() }
    }
}
