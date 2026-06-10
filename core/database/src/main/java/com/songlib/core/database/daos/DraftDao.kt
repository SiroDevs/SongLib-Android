package com.songlib.core.database.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.songlib.core.database.model.DraftEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DraftDao {
    @Query("SELECT * FROM drafts ORDER BY created DESC")
    fun getAllFlow(): Flow<List<DraftEntity>>

    @Query("SELECT * FROM drafts ORDER BY created DESC")
    suspend fun getAll(): List<DraftEntity>

    @Query("SELECT * FROM drafts WHERE id = :id")
    suspend fun getById(id: Int): DraftEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(draft: DraftEntity): Long

    @Update
    suspend fun update(draft: DraftEntity)

    @Query("DELETE FROM drafts WHERE id = :id")
    suspend fun deleteById(id: Int)

    @Query("DELETE FROM drafts")
    suspend fun deleteAll()
}
