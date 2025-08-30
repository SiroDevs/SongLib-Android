package com.songlib.data.models

import androidx.annotation.Keep
import androidx.room.*
import com.songlib.core.utils.DbConstants
import java.util.Date

@Keep
@Entity(tableName = DbConstants.SEARCHES)
data class Search(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "created") val created: String
)
