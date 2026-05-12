package com.songlib.core.database.daos

import androidx.room.*
import com.songlib.core.common.utils.DbConstants
import com.songlib.core.database.model.History

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

    @Query("DELETE FROM ${DbConstants.HISTORIES} WHERE id = :id")
    suspend fun deleteById(id: Int)

    @Query("DELETE FROM ${DbConstants.HISTORIES}")
    suspend fun deleteAll()

}