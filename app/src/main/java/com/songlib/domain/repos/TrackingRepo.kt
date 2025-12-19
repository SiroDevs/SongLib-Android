package com.songlib.domain.repository

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
    private var historyDao: HistoryDao?
    private var searchDao: SearchDao?

    init {
        val db = AppDatabase.getDatabase(context)
        historyDao = db?.historyDao()
        searchDao = db?.searchDao()
    }

    suspend fun saveHistory(history: History) {
        withContext(Dispatchers.IO) {
            historyDao?.insert(history)
        }
    }

    suspend fun saveSearch(search: Search) {
        withContext(Dispatchers.IO) {
            searchDao?.insert(search)
        }
    }

    suspend fun fetchHistories(): List<History> {
        var allHistories: List<History>
        withContext(Dispatchers.IO) {
            allHistories = historyDao?.getAll() ?: emptyList()
        }
        return allHistories
    }

    suspend fun fetchSearches(): List<Search> {
        var allSearches: List<Search>
        withContext(Dispatchers.IO) {
            allSearches = searchDao?.getAll() ?: emptyList()
        }
        return allSearches
    }

    suspend fun updateSearch(search: Search) {
        withContext(Dispatchers.IO) {
            searchDao?.update(search)
        }
    }

    suspend fun deleteHistoryById(id: Int) {
        withContext(Dispatchers.IO) {
            historyDao?.deleteById(id)
        }
    }

    suspend fun deleteAllHistories() {
        withContext(Dispatchers.IO) {
            historyDao?.deleteAll()
        }
    }

    suspend fun deleteSearchById(id: Int) {
        withContext(Dispatchers.IO) {
            searchDao?.deleteById(id)
        }
    }

    suspend fun deleteAllSearches() {
        withContext(Dispatchers.IO) {
            searchDao?.deleteAll()
        }
    }

}