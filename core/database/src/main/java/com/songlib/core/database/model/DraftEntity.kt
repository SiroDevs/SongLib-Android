package com.songlib.core.database.model

import android.os.Parcelable
import androidx.annotation.Keep
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
@Entity(tableName = "feature")
data class DraftEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "draftId")  val draftId: Int = 0,
    @ColumnInfo(name = "title")    val title: String,
    @ColumnInfo(name = "content")  val content: String = "",
    @ColumnInfo(name = "songNo")   val songNo: Int? = null,
    @ColumnInfo(name = "book")     val book: Int? = null,
    @ColumnInfo(name = "userId")   val userId: Int = 0,
    @ColumnInfo(name = "created")  val created: String,
    @ColumnInfo(name = "updated")  val updated: String? = null,
    @ColumnInfo(name = "synced")   val synced: Boolean = false
) : Parcelable
