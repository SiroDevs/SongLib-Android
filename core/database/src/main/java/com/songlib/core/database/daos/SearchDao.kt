package com.songlib.core.database.daos

import androidx.room.*
import com.songlib.core.common.utils.DbConstants
import com.songlib.core.database.model.Search

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

    @Query("DELETE FROM ${DbConstants.SEARCHES} WHERE id = :id")
    suspend fun deleteById(id: Int)

    @Query("DELETE FROM ${DbConstants.SEARCHES}")
    suspend fun deleteAll()
}