package com.songlib.data.sources.local.daos

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.songlib.core.utils.DbConstants
import com.songlib.data.models.Song

interface HistoryDao {
    @Query("SELECT * FROM ${DbConstants.HISTORIES}")
    fun getAll(): List<Song>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(history: Song)

    @Update()
    fun update(history: Song)

    @Delete()
    fun delete(history: Song)

    @Query("DELETE FROM ${DbConstants.HISTORIES}")
    suspend fun deleteAll()

    @Query("DELETE FROM ${DbConstants.HISTORIES} WHERE id = :id")
    suspend fun deleteById(id: Int)
}