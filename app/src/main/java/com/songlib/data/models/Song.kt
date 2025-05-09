package com.songlib.data.models

import androidx.annotation.Keep
import androidx.room.*

@Keep
@Entity(indices = [Index(value = ["songId"], unique = true)])
data class Song(
    @ColumnInfo(name = "alias") val alias: String,
    @ColumnInfo(name = "book") val book: Int,
    @ColumnInfo(name = "content") val content: String,
    @ColumnInfo(name = "created") val created: String,
    @ColumnInfo(name = "liked") val liked: Boolean,
    @ColumnInfo(name = "likes") val likes: Int,
    @PrimaryKey() val songId: Int,
    @ColumnInfo(name = "songNo") val songNo: Int,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "views") val views: Int
)

data class SongResp(
    val count: Int,
    val `data`: List<Song>
)