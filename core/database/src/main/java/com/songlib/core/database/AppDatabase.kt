package com.songlib.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.songlib.core.database.daos.BookDao
import com.songlib.core.database.daos.DraftDao
import com.songlib.core.database.daos.EditDao
import com.songlib.core.database.daos.HistoryDao
import com.songlib.core.database.daos.ListingDao
import com.songlib.core.database.daos.SearchDao
import com.songlib.core.database.daos.SongDao
import com.songlib.core.database.model.BookEntity
import com.songlib.core.database.model.DraftEntity
import com.songlib.core.database.model.EditEntity
import com.songlib.core.database.model.HistoryEntity
import com.songlib.core.database.model.ListingEntity
import com.songlib.core.database.model.SearchEntity
import com.songlib.core.database.model.SongEntity

@Database(
    entities = [
        BookEntity::class,
        HistoryEntity::class,
        ListingEntity::class,
        SearchEntity::class,
        SongEntity::class,
        DraftEntity::class,
        EditEntity::class,
    ],
    version = 4,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun booksDao(): BookDao
    abstract fun historiesDao(): HistoryDao
    abstract fun listingsDao(): ListingDao
    abstract fun searchesDao(): SearchDao
    abstract fun songsDao(): SongDao
    abstract fun draftsDao(): DraftDao
    abstract fun editsDao(): EditDao
}
