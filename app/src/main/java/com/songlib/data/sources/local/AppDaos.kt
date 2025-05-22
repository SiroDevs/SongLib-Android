package com.songlib.data.sources.local

import androidx.room.*
import com.songlib.data.models.*

@Dao
interface BookDao {
    @Query("SELECT * FROM books")
    fun getAll(): List<Book>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(book: Book)

    @Update()
    fun update(book: Book)

    @Delete()
    fun delete(book: Book)
}

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