package com.songlib.core.database.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.songlib.core.database.model.SearchEntity

@Dao
interface SearchDao {
    @Query("SELECT * FROM searches")
    fun getAll(): List<SearchEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(search: SearchEntity)

    @Update()
    fun update(search: SearchEntity)

    @Delete()
    fun delete(search: SearchEntity)

    @Query("DELETE FROM searches WHERE id = :id")
    suspend fun deleteById(id: Int)

    @Query("DELETE FROM searches")
    suspend fun deleteAll()
}