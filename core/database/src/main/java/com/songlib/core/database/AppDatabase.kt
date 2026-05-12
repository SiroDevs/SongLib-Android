package com.songlib.core.database

import android.content.Context
import androidx.room.*
import com.songlib.core.database.model.*
import com.songlib.core.database.daos.*

@Database(
    entities = [Book::class, History::class, Listing::class, Search::class, Song::class],
    version = 2, exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun booksDao(): BookDao
    abstract fun historiesDao(): HistoryDao
    abstract fun listingsDao(): ListingDao
    abstract fun searchesDao(): SearchDao
    abstract fun songsDao(): SongDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase? {
            if (INSTANCE == null) {
                synchronized(AppDatabase::class.java) {
                    if (INSTANCE == null) {
                        INSTANCE = Room.databaseBuilder(
                            context.applicationContext,
                            AppDatabase::class.java, "SongLib"
                        ).fallbackToDestructiveMigration(false).build()
                    }
                }
            }
            return INSTANCE
        }
    }
}