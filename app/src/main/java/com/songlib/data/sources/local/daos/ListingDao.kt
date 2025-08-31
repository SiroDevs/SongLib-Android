package com.songlib.data.sources.local.daos

import androidx.room.*
import com.songlib.core.utils.DbConstants
import com.songlib.data.models.Listing

@Dao
interface ListingDao {
    @Query("SELECT * FROM ${DbConstants.LISTINGS}")
    fun getAll(): List<Listing>

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