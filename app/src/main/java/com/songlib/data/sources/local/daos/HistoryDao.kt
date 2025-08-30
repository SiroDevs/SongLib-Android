package com.songlib.data.sources.local.daos

import androidx.room.*
import com.songlib.core.utils.DbConstants
import com.songlib.data.models.History

@Dao
interface HistoryDao {
    @Query("SELECT * FROM ${DbConstants.HISTORIES}")
    fun getAll(): List<History>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(history: History)

    @Update()
    fun update(history: History)

    @Delete()
    fun delete(history: History)

    @Query("DELETE FROM ${DbConstants.HISTORIES}")
    suspend fun deleteAll()

    @Query("DELETE FROM ${DbConstants.HISTORIES} WHERE id = :id")
    suspend fun deleteById(id: Int)
}