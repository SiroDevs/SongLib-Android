package com.songlib.core.data.repos

import com.songlib.core.common.utils.toTimeAgo
import com.songlib.core.database.model.ListingEntity
import com.songlib.core.database.daos.ListingDao
import com.songlib.core.database.model.ListingUi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ListingRepo @Inject constructor(private var listingsDao: ListingDao) {
    suspend fun fetchListings(parent: Int): List<ListingUi> {
        return withContext(Dispatchers.IO) {
            val allListings = listingsDao.getAll(parent) ?: emptyList()

            allListings.map { listing ->
                ListingUi(
                    id = listing.id,
                    parent = listing.parent,
                    title = listing.title,
                    song = listing.song,
                    created = listing.created,
                    modified = listing.modified,
                    songCount = listingsDao.countSongs(listing.id) ?: 0,
                    updatedAgo = listing.modified.toLongOrNull()?.toTimeAgo() ?: ""
                )
            }
        }
    }

    suspend fun saveListing(parent: Int, title: String, song: Int) {
        withContext(Dispatchers.IO) {
            val currentTime = System.currentTimeMillis().toString()
            val newListing = ListingEntity(
                parent = parent,
                title = title,
                song = song,
                created = currentTime,
                modified = currentTime
            )
            listingsDao.insert(newListing)
        }
    }

    suspend fun saveListItem(parent: ListingUi, song: Int) {
        withContext(Dispatchers.IO) {
            val currentTime = System.currentTimeMillis().toString()
            listingsDao.insert(ListingEntity(
                parent = parent.id,
                title = "",
                song = song,
                created = currentTime,
                modified = currentTime
            ))
            listingsDao.update(ListingEntity(
                parent = parent.id,
                title = parent.title,
                song = song,
                created = parent.created,
                modified = currentTime
            ))
        }
    }

    suspend fun updateListing(listing: ListingUi) {
        val currentTime = System.currentTimeMillis().toString()
        withContext(Dispatchers.IO) {
            listingsDao.update(ListingEntity(
                parent = listing.id,
                title = listing.title,
                song = listing.song,
                created = listing.created,
                modified = currentTime
            ))
        }
    }

    suspend fun deleteById(listId: Int) {
        withContext(Dispatchers.IO) {
            listingsDao.deleteById(listId)
        }
    }

    suspend fun deleteAllListings() {
        withContext(Dispatchers.IO) {
            listingsDao.deleteAll()
        }
    }
}