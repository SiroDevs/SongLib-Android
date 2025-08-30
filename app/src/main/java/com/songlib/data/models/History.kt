package com.songlib.data.models

import androidx.room.*
import java.util.Date

@Entity(tableName = "histories")
data class History(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "song") val song: Int = 0,
    @ColumnInfo(name = "created") val created: Date
)
