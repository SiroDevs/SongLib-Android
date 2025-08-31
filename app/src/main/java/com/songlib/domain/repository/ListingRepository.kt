package com.songlib.domain.repository

import android.content.*
import com.songlib.data.models.*
import com.songlib.data.sources.local.*
import com.songlib.data.sources.local.daos.*
import kotlinx.coroutines.*
import javax.inject.*

@Singleton
class ListingRepository @Inject constructor(context: Context) {
    private var listDao: ListingDao?

    init {
        val db = AppDatabase.getDatabase(context)
        listDao = db?.listingDao()
    }

    suspend fun saveListing(listing: Listing) {
        withContext(Dispatchers.IO) {
            listDao?.insert(listing)
        }
    }

    suspend fun fetchListings(): List<Listing> {
        var allListings: List<Listing>
        withContext(Dispatchers.IO) {
            allListings = listDao?.getAll() ?: emptyList()
        }
        return allListings
    }

    suspend fun updateListing(listing: Listing) {
        withContext(Dispatchers.IO) {
            listDao?.update(listing)
        }
    }

    suspend fun deleteById(listId: Int) {
        withContext(Dispatchers.IO) {
            listDao?.deleteById(listId)
        }
    }

    suspend fun deleteAllListings() {
        withContext(Dispatchers.IO) {
            listDao?.deleteAll()
        }
    }

}