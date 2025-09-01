package com.songlib.data.models

import androidx.annotation.Keep
import androidx.room.*
import com.songlib.core.utils.DbConstants

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

data class ListingUi(
    val id: Int,
    val parent: Int,
    val title: String,
    val song: Int,
    val created: String,
    val modified: String,
    val songCount: Int,
    val updatedAgo: String
)
