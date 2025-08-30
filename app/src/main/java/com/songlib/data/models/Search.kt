package com.songlib.data.models

import androidx.room.*
import java.util.Date

@Entity(tableName = "searches")
data class Search(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "created") val created: Date
)
