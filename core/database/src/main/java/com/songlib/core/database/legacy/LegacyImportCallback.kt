package com.songlib.core.database.legacy

import android.content.Context
import android.util.Log
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * One-time import of data from the old "SongLib" Room database into the
 * freshly created "SongLibrary" database.
 *
 * Room only calls onCreate() when the database FILE does not already exist
 * on disk. That gives us a free, reliable signal to tell the three cases apart:
 *
 *  - Fresh install:     "SongLibrary" doesn't exist AND "SongLib" doesn't exist -> nothing to do.
 *  - App update:        "SongLibrary" doesn't exist BUT "SongLib" does         -> copy it over.
 *  - Already imported:  "SongLibrary" already exists                          -> onCreate never fires again.
 *
 * No AppDatabase migrations are needed for this: "SongLibrary" starts clean at
 * version 1, so there's nothing broken to inherit going forward.
 */
class LegacyImportCallback(
    private val context: Context
) : RoomDatabase.Callback() {

    override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)

        val oldDbFile = context.getDatabasePath(OLD_DB_NAME)
        if (!oldDbFile.exists()) {
            // Fresh install - there is no legacy data to bring across.
            return
        }

        try {
            db.execSQL("ATTACH DATABASE '${oldDbFile.absolutePath}' AS $OLD_DB_ALIAS")
            db.beginTransaction()
            try {
                TABLES.forEach { table -> copyTable(db, table) }
                db.setTransactionSuccessful()
            } finally {
                db.endTransaction()
            }
        } catch (e: Exception) {
            // A malformed legacy DB must never take the new app down.
            // Worst case: the user starts with an empty library the way a
            // fresh install would, and can re-download/re-sync as usual.
            Log.e(TAG, "Legacy SongLib import failed, continuing with empty SongLibrary", e)
        } finally {
            runCatching { db.execSQL("DETACH DATABASE $OLD_DB_ALIAS") }
            // Deliberately NOT deleting/renaming SongLib.db here. Leaving it
            // untouched costs a few KB of disk and means a bug in this
            // importer is recoverable later (re-attach and re-run by hand,
            // or inspect via adb) rather than a silent, permanent data loss.
            // Clean it up in a later release once you trust this path.
        }
    }

    /**
     * Copies only the columns that exist in BOTH the old and new versions of
     * a table. This is what makes the copy resilient to the historical mess:
     * whatever a given user's SongLib.db actually looks like on disk right
     * now (some are missing columns because of the bad 1->2 / 3->4 / 5->6
     * migrations, some may be on an even older shape), only the columns that
     * safely map onto the new schema get pulled across. Anything else falls
     * back to that column's default in the new table.
     */
    private fun copyTable(db: SupportSQLiteDatabase, table: String) {
        if (!tableExists(db, OLD_DB_ALIAS, table)) return

        val oldColumns = columnsOf(db, OLD_DB_ALIAS, table)
        val newColumns = columnsOf(db, "main", table)
        val common = newColumns.filter { it in oldColumns }
        if (common.isEmpty()) return

        val columnList = common.joinToString(", ") { "\"$it\"" }
        db.execSQL(
            "INSERT OR IGNORE INTO \"$table\" ($columnList) " +
                "SELECT $columnList FROM $OLD_DB_ALIAS.\"$table\""
        )
    }

    private fun tableExists(db: SupportSQLiteDatabase, schema: String, table: String): Boolean {
        db.query(
            "SELECT name FROM $schema.sqlite_master WHERE type='table' AND name=?",
            arrayOf(table)
        ).use { cursor -> return cursor.moveToFirst() }
    }

    private fun columnsOf(db: SupportSQLiteDatabase, schema: String, table: String): Set<String> {
        val columns = mutableSetOf<String>()
        db.query("PRAGMA $schema.table_info(\"$table\")").use { cursor ->
            val nameIndex = cursor.getColumnIndex("name")
            while (cursor.moveToNext()) {
                columns += cursor.getString(nameIndex)
            }
        }
        return columns
    }

    companion object {
        private const val TAG = "LegacyImportCallback"
        private const val OLD_DB_NAME = "SongLib"
        private const val OLD_DB_ALIAS = "old_db"

        // No FK constraints between these tables, so order doesn't matter.
        // Kept in sync with AppDatabase's @Database(entities = [...]) list.
        private val TABLES = listOf(
            "books", "songs", "histories", "listings", "searches", "feature", "edits"
        )
    }
}
