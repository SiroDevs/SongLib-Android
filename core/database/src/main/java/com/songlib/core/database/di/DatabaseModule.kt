package com.songlib.core.database.di

import android.content.Context
import androidx.room.Room
import com.songlib.core.database.AppDatabase
import com.songlib.core.database.daos.BookDao
import com.songlib.core.database.daos.HistoryDao
import com.songlib.core.database.daos.ListingDao
import com.songlib.core.database.daos.SearchDao
import com.songlib.core.database.daos.SongDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object DatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext appContext: Context): AppDatabase =
        Room.databaseBuilder(appContext, AppDatabase::class.java, "SongLib")
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    fun provideBookDao(db: AppDatabase): BookDao = db.booksDao()

    @Provides
    fun provideHistoryDao(db: AppDatabase): HistoryDao = db.historiesDao()

    @Provides
    fun provideListingDao(db: AppDatabase): ListingDao = db.listingsDao()

    @Provides
    fun provideSearchDao(db: AppDatabase): SearchDao = db.searchesDao()

    @Provides
    fun provideSongDao(db: AppDatabase): SongDao = db.songsDao()
}
