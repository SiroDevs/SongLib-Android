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
    private var listingDao: ListingDao?

    init {
        val db = AppDatabase.getDatabase(context)
        listingDao = db?.listingDao()
    }

    suspend fun fetchListings(parent: Int): List<ListingUi> {
        return withContext(Dispatchers.IO) {
            val allListings = listingDao?.getAll(parent) ?: emptyList()

            allListings.map { listing ->
                ListingUi(
                    id = listing.id,
                    parent = listing.parent,
                    title = listing.title,
                    song = listing.song,
                    created = listing.created,
                    modified = listing.modified,
                    songCount = listingDao?.countSongs(listing.id) ?: 0,
                    updatedAgo = listing.modified.toLongOrNull()?.toTimeAgo() ?: ""
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
            listingDao?.insert(newListing)
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
            listingDao?.insert(newListing)
            listingDao?.update(parent)
        }
    }

    suspend fun updateListing(listing: Listing) {
        withContext(Dispatchers.IO) {
            listingDao?.update(listing)
        }
    }

    suspend fun deleteById(listId: Int) {
        withContext(Dispatchers.IO) {
            listingDao?.deleteById(listId)
        }
    }

    suspend fun deleteAllListings() {
        withContext(Dispatchers.IO) {
            listingDao?.deleteAll()
        }
    }

}