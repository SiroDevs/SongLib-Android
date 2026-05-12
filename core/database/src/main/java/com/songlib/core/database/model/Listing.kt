package com.songlib.core.database.model

import android.os.Parcelable
import androidx.annotation.Keep
import androidx.room.*
import com.songlib.core.common.utils.DbConstants
import kotlinx.parcelize.Parcelize

@Keep
@Entity(tableName = DbConstants.LISTINGS)
data class Listing(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val parent: Int,
    val title: String,
    val song: Int,
    val created: String,
    val modified: String,
)

@Parcelize
data class ListingUi(
    val id: Int,
    val parent: Int,
    val title: String,
    val song: Int,
    val created: String,
    val modified: String,
    val songCount: Int,
    val updatedAgo: String
) : Parcelable
