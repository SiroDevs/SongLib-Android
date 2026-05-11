package com.songlib.domain.repos

import android.content.*
import com.songlib.data.models.*
import com.songlib.data.sources.local.*
import com.songlib.data.sources.local.daos.*
import kotlinx.coroutines.*
import javax.inject.*

@Singleton
class TrackingRepo @Inject constructor(
    context: Context,
) {
    private var historiesDao: HistoryDao?
    private var searchesDao: SearchDao?

    init {
        val db = AppDatabase.getDatabase(context)
        historiesDao = db?.historiesDao()
        searchesDao = db?.searchesDao()
    }

    suspend fun saveHistory(history: History) {
        withContext(Dispatchers.IO) {
            historiesDao?.insert(history)
        }
    }

    suspend fun saveSearch(search: Search) {
        withContext(Dispatchers.IO) {
            searchesDao?.insert(search)
        }
    }

    suspend fun fetchHistories(): List<History> {
        var allHistories: List<History>
        withContext(Dispatchers.IO) {
            allHistories = historiesDao?.getAll() ?: emptyList()
        }
        return allHistories
    }

    suspend fun fetchSearches(): List<Search> {
        var allSearches: List<Search>
        withContext(Dispatchers.IO) {
            allSearches = searchesDao?.getAll() ?: emptyList()
        }
        return allSearches
    }

    suspend fun updateSearch(search: Search) {
        withContext(Dispatchers.IO) {
            searchesDao?.update(search)
        }
    }

    suspend fun deleteHistoryById(id: Int) {
        withContext(Dispatchers.IO) {
            historiesDao?.deleteById(id)
        }
    }

    suspend fun deleteAllHistories() {
        withContext(Dispatchers.IO) {
            historiesDao?.deleteAll()
        }
    }

    suspend fun deleteSearchById(id: Int) {
        withContext(Dispatchers.IO) {
            searchesDao?.deleteById(id)
        }
    }

    suspend fun deleteAllSearches() {
        withContext(Dispatchers.IO) {
            searchesDao?.deleteAll()
        }
    }

}