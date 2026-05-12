package com.songlib.core.database.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.songlib.core.database.model.SongEntity

@Dao
interface SongDao {
    @Query("SELECT * FROM songs")
    fun getAll(): List<SongEntity>

    @Query("SELECT * FROM songs WHERE songId = :songId")
    fun getSong(songId: Int): SongEntity

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(song: SongEntity)

    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insertAll(songs: List<SongEntity>)

    @Update()
    fun update(song: SongEntity)

    @Delete()
    fun delete(song: SongEntity)

    @Query("DELETE FROM songs WHERE book = :id")
    suspend fun deleteById(id: Int)

    @Query("DELETE FROM songs")
    suspend fun deleteAll()
}