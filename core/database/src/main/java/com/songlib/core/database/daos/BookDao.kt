package com.songlib.core.database.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.songlib.core.database.model.BookEntity

@Dao
interface BookDao {
    @Query("SELECT * FROM books")
    fun getAll(): List<BookEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(book: BookEntity)

    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insertAll(books: List<BookEntity>)

    @Update()
    fun update(book: BookEntity)

    @Delete()
    fun delete(book: BookEntity)

    @Query("DELETE FROM books WHERE bookId = :id")
    suspend fun deleteById(id: Int)

    @Query("DELETE FROM books")
    suspend fun deleteAll()
}