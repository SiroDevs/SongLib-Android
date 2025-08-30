package com.songlib.data.sources.local.daos

import androidx.room.*
import com.songlib.core.utils.DbConstants
import com.songlib.data.models.Search

@Dao
interface SearchDao {
    @Query("SELECT * FROM ${DbConstants.SEARCHES}")
    fun getAll(): List<Search>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(search: Search)

    @Update()
    fun update(search: Search)

    @Delete()
    fun delete(search: Search)

    @Query("DELETE FROM ${DbConstants.SEARCHES}")
    suspend fun deleteAll()

    @Query("DELETE FROM ${DbConstants.SEARCHES} WHERE id = :id")
    suspend fun deleteById(id: Int)
}