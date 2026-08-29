package com.songlib.core.database.model

import androidx.annotation.Keep
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Learned Auto Play timing for a given song — how long to linger on a verse
 * page versus a chorus page before automatically advancing to the next stanza.
 */
@Keep
@Entity(tableName = "auto_play")
data class AutoPlayEntity(
    @PrimaryKey @ColumnInfo(name = "songId") val songId: Int,
    @ColumnInfo(name = "verse_duration") val verseDuration: Long,
    @ColumnInfo(name = "chorus_duration") val chorusDuration: Long,
)
