package com.songlib.data.sources.local.daos

import androidx.room.*
import com.songlib.core.utils.DbConstants
import com.songlib.data.models.Book

@Dao
interface BookDao {
    @Query("SELECT * FROM ${DbConstants.BOOKS}")
    fun getAll(): List<Book>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(book: Book)

    @Update()
    fun update(book: Book)

    @Delete()
    fun delete(book: Book)

    @Query("DELETE FROM ${DbConstants.BOOKS}")
    suspend fun deleteAll()

    @Query("DELETE FROM ${DbConstants.BOOKS} WHERE bookId = :id")
    suspend fun deleteById(id: Int)
}