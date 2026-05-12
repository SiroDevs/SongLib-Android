package com.songlib.core.database.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.songlib.core.database.model.ListingEntity

@Dao
interface ListingDao {
    @Query("SELECT * FROM listings WHERE parent = :parent ORDER BY modified DESC")
    fun getAll(parent: Int): List<ListingEntity>

    @Query("SELECT COUNT(*) FROM listings WHERE parent = :parentId")
    suspend fun countSongs(parentId: Int): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(listing: ListingEntity)

    @Update()
    fun update(listing: ListingEntity)

    @Delete()
    fun delete(listing: ListingEntity)

    @Query("DELETE FROM listings WHERE id = :id")
    suspend fun deleteById(id: Int)

    @Query("DELETE FROM listings")
    suspend fun deleteAll()

}