package com.songlib.core.database.model

import androidx.annotation.Keep
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Keep
@Entity(tableName = "searches", indices = [Index(value = ["title"], unique = true)])
data class SearchEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "title")   val title: String,
    @ColumnInfo(name = "hits")    val hits: Int = 1,
    @ColumnInfo(name = "created") val created: String
)
