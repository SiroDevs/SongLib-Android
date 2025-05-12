package com.songlib.data.models

import androidx.annotation.Keep
import androidx.room.*

@Keep
@Entity(indices = [Index(value = ["bookId"], unique = true)])
data class Book(
    @PrimaryKey() val bookId: Int,
    @ColumnInfo(name = "bookNo") val bookNo: Int,
    @ColumnInfo(name = "created") val created: String,
    @ColumnInfo(name = "enabled") val enabled: Boolean,
    @ColumnInfo(name = "position") val position: Int,
    @ColumnInfo(name = "songs") val songs: Int,
    @ColumnInfo(name = "subTitle") val subTitle: String,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "user") val user: Int
)

data class BooksResponse(
    val count: Int,
    val `data`: List<Book>
)
