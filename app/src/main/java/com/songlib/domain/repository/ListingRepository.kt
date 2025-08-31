package com.songlib.domain.repository

import android.content.*
import com.songlib.core.utils.toTimeAgo
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

    suspend fun fetchListings(parent: Int): List<Listing> {
        return withContext(Dispatchers.IO) {
            val allListings = listDao?.getAll(parent) ?: emptyList()

            allListings.map { listing ->
                Listing(
                    id = listing.id,
                    parent = listing.parent,
                    title = listing.title,
                    created = listing.created,
                    song = listDao?.countSongs(listing.id) ?: 0,
                    modified = listing.modified.toLongOrNull()?.toTimeAgo() ?: ""
                )
            }
        }
    }

    suspend fun saveListing(parent: Int, title: String, song: Int) {
        withContext(Dispatchers.IO) {
            val currentTime = System.currentTimeMillis().toString()
            val newListing = Listing(
                parent = parent,
                title = title,
                song = song,
                created = currentTime,
                modified = currentTime
            )
            listDao?.insert(newListing)
        }
    }

    suspend fun saveListItem(parent: Listing, song: Int) {
        withContext(Dispatchers.IO) {
            val currentTime = System.currentTimeMillis().toString()
            val newListing = Listing(
                parent = parent.id,
                title = "",
                song = song,
                created = currentTime,
                modified = currentTime
            )
            listDao?.insert(newListing)
            listDao?.update(parent)
        }
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