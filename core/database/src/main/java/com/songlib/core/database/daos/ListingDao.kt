package com.songlib.core.database.daos

import androidx.room.*
import com.songlib.core.common.utils.DbConstants
import com.songlib.core.database.model.Listing

@Dao
interface ListingDao {
    @Query("SELECT * FROM ${DbConstants.LISTINGS} WHERE parent = :parent ORDER BY modified DESC")
    fun getAll(parent: Int): List<Listing>

    @Query("SELECT COUNT(*) FROM listings WHERE parent = :parentId")
    suspend fun countSongs(parentId: Int): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(listing: Listing)

    @Update()
    fun update(listing: Listing)

    @Delete()
    fun delete(listing: Listing)

    @Query("DELETE FROM ${DbConstants.LISTINGS} WHERE id = :id")
    suspend fun deleteById(id: Int)

    @Query("DELETE FROM ${DbConstants.LISTINGS}")
    suspend fun deleteAll()

}