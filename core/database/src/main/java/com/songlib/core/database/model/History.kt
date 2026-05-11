package com.songlib.data.models

import androidx.annotation.Keep
import androidx.room.*
import com.songlib.core.utils.DbConstants

@Keep
@Entity(tableName = DbConstants.HISTORIES)
data class History(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "song") val song: Int = 0,
    @ColumnInfo(name = "created") val created: String
)
