package com.songlib.data.sources.local.daos

import androidx.room.*
import com.songlib.data.models.Book

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