package com.songlib.core.data.repos

import com.songlib.core.database.daos.HistoryDao
import com.songlib.core.database.daos.SearchDao
import com.songlib.core.database.model.HistoryEntity
import com.songlib.core.database.model.SearchEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TrackingRepo @Inject constructor(
    private var historiesDao: HistoryDao,
    private var searchesDao: SearchDao
) {
    suspend fun saveHistory(history: HistoryEntity) {
        withContext(Dispatchers.IO) {
            historiesDao.insert(history)
        }
    }

    suspend fun saveSearch(search: SearchEntity) {
        withContext(Dispatchers.IO) {
            searchesDao.insert(search)
        }
    }

    suspend fun fetchHistories(): List<HistoryEntity> {
        var allHistories: List<HistoryEntity>
        withContext(Dispatchers.IO) {
            allHistories = historiesDao.getAll() ?: emptyList()
        }
        return allHistories
    }

    suspend fun fetchSearches(): List<SearchEntity> {
        var allSearches: List<SearchEntity>
        withContext(Dispatchers.IO) {
            allSearches = searchesDao.getAll() ?: emptyList()
        }
        return allSearches
    }

    suspend fun updateSearch(search: SearchEntity) {
        withContext(Dispatchers.IO) {
            searchesDao.update(search)
        }
    }

    suspend fun deleteHistoryById(id: Int) {
        withContext(Dispatchers.IO) {
            historiesDao.deleteById(id)
        }
    }

    suspend fun deleteAllHistories() {
        withContext(Dispatchers.IO) {
            historiesDao.deleteAll()
        }
    }

    suspend fun deleteSearchById(id: Int) {
        withContext(Dispatchers.IO) {
            searchesDao.deleteById(id)
        }
    }

    suspend fun deleteAllSearches() {
        withContext(Dispatchers.IO) {
            searchesDao.deleteAll()
        }
    }

}