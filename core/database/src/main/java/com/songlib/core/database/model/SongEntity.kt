package com.songlib.core.database.model

import android.os.Parcelable
import androidx.annotation.Keep
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
@Entity(tableName = "songs", indices = [Index(value = ["songId"], unique = true)])
data class SongEntity(
    @PrimaryKey() val songId: Int,
    @ColumnInfo(name = "alias") val alias: String,
    @ColumnInfo(name = "book") val book: Int,
    @ColumnInfo(name = "content") val content: String,
    @ColumnInfo(name = "created") val created: String,
    @ColumnInfo(name = "liked") val liked: Boolean,
    @ColumnInfo(name = "likes") val likes: Int,
    @ColumnInfo(name = "songNo") val songNo: Int,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "views") val views: Int
): Parcelable
