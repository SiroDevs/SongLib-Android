package com.songlib.core.database.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * Adds the `auto_play` table used to store the learned Auto Play verse/chorus
 * stanza durations per song (see [com.songlib.core.database.model.AutoPlayEntity]).
 */
val Migration1To2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `auto_play` (
                `songId` INTEGER NOT NULL,
                `verse_duration` INTEGER NOT NULL,
                `chorus_duration` INTEGER NOT NULL,
                PRIMARY KEY(`songId`)
            )
            """.trimIndent()
        )
    }
}
