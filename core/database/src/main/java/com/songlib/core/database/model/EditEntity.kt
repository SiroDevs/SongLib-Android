package com.songlib.core.database.model

import androidx.annotation.Keep
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Keep
@Entity(tableName = "edits")
data class EditEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "editId")  val editId: Int = 0,
    @ColumnInfo(name = "songId")  val songId: Int,
    @ColumnInfo(name = "title")   val title: String,
    @ColumnInfo(name = "content") val content: String = "",
    @ColumnInfo(name = "userId")  val userId: Int = 0,
    @ColumnInfo(name = "status")  val status: String = "pending",
    @ColumnInfo(name = "created") val created: String,
    @ColumnInfo(name = "updated") val updated: String? = null,
    @ColumnInfo(name = "synced")  val synced: Boolean = false
)
