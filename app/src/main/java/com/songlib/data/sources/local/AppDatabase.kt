package com.songlib.data.sources.local

import android.content.Context
import androidx.room.*
import com.songlib.data.models.*

@Database(entities = [Book::class, Song::class], version = 3, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun booksDao(): BookDao
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
                        ).fallbackToDestructiveMigration().build()
                    }
                }
            }
            return INSTANCE
        }
    }
}