package com.songlib.data.models

import android.os.Parcelable
import androidx.annotation.Keep
import androidx.room.*
import com.songlib.core.utils.DbConstants
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
@Entity(tableName = DbConstants.SONGS, indices = [Index(value = ["songId"], unique = true)])
data class Song(
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
