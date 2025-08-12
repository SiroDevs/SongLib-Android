package com.songlib.data.sources.local.daos

import androidx.room.*
import com.songlib.data.models.Song

@Dao
interface SongDao {
    @Query("SELECT * FROM songs")
    fun getAll(): List<Song>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(song: Song)

    @Update()
    fun update(song: Song)

    @Delete()
    fun delete(song: Song)
}