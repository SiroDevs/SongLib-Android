package com.songlib.data.sources.local

import androidx.room.*
import com.songlib.data.models.*

@Dao
interface BookDao {
    @Query("SELECT * FROM book ")
    fun getAll(): List<Book>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg books: Book)

    @Update()
    fun update(book: Book)

    @Delete()
    fun delete(book: Book)
}

@Dao
interface SongDao {
    @Query("SELECT * FROM song ")
    fun getAll(): List<Song>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg songs: Song)

    @Update()
    fun update(song: Song)

    @Delete()
    fun delete(song: Song)
}