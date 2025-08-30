package com.songlib.data.sources.local.daos

import androidx.room.*
import com.songlib.core.utils.DbConstants
import com.songlib.data.models.Song

@Dao
interface SongDao {
    @Query("SELECT * FROM ${DbConstants.SONGS}")
    fun getAll(): List<Song>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(song: Song)

    @Update()
    fun update(song: Song)

    @Delete()
    fun delete(song: Song)

    @Query("DELETE FROM ${DbConstants.SONGS}")
    suspend fun deleteAll()

    @Query("DELETE FROM ${DbConstants.SONGS} WHERE songId = :id")
    suspend fun deleteById(id: Int)

    @Query("DELETE FROM ${DbConstants.SONGS} WHERE book = :id")
    suspend fun deleteByBookId(id: Int)
}