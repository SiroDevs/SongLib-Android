package com.songlib.data.sources.local

import android.content.Context
import androidx.room.*
import com.songlib.data.models.*
import com.songlib.data.sources.local.daos.*

@Database(entities = [Book::class, Song::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun bookDao(): BookDao
    abstract fun listingDao(): ListingDao
    abstract fun historyDao(): HistoryDao
    abstract fun searchDao(): SearchDao
    abstract fun songDao(): SongDao

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