package com.songlib.core.database.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.songlib.core.database.model.HistoryEntity

@Dao
interface HistoryDao {
    @Query("SELECT * FROM histories")
    fun getAll(): List<HistoryEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(history: HistoryEntity)

    @Update()
    fun update(history: HistoryEntity)

    @Delete()
    fun delete(history: HistoryEntity)

    @Query("DELETE FROM histories WHERE id = :id")
    suspend fun deleteById(id: Int)

    @Query("DELETE FROM histories")
    suspend fun deleteAll()

}