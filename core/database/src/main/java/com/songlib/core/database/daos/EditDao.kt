package com.songlib.core.database.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.songlib.core.database.model.EditEntity

@Dao
interface EditDao {
    @Query("SELECT * FROM edits WHERE userId = :userId ORDER BY created DESC")
    suspend fun getForUser(userId: Int): List<EditEntity>

    @Query("SELECT * FROM edits ORDER BY created DESC")
    suspend fun getAll(): List<EditEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(edit: EditEntity): Long

    @Update
    suspend fun update(edit: EditEntity)

    @Query("DELETE FROM edits WHERE id = :id")
    suspend fun deleteById(id: Int)

    @Query("SELECT COUNT(*) FROM edits WHERE userId = :userId")
    suspend fun countForUser(userId: Int): Int
}
