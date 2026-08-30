package com.songlib.core.database.di

import android.content.Context
import androidx.room.Room
import com.songlib.core.database.AppDatabase
import com.songlib.core.database.daos.AutoPlayDao
import com.songlib.core.database.daos.BookDao
import com.songlib.core.database.daos.DraftDao
import com.songlib.core.database.daos.EditDao
import com.songlib.core.database.daos.HistoryDao
import com.songlib.core.database.daos.ListingDao
import com.songlib.core.database.daos.SearchDao
import com.songlib.core.database.daos.SongDao
import com.songlib.core.database.legacy.LegacyImportCallback
import com.songlib.core.database.migrations.ALL_MIGRATIONS
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
        Room.databaseBuilder(appContext, AppDatabase::class.java, "SongLibrary")
            .addCallback(LegacyImportCallback(appContext))
            .addMigrations(*ALL_MIGRATIONS)
            .build()

    @Provides fun provideBookDao(db: AppDatabase): BookDao = db.booksDao()
    @Provides fun provideHistoryDao(db: AppDatabase): HistoryDao = db.historiesDao()
    @Provides fun provideListingDao(db: AppDatabase): ListingDao = db.listingsDao()
    @Provides fun provideSearchDao(db: AppDatabase): SearchDao = db.searchesDao()
    @Provides fun provideSongDao(db: AppDatabase): SongDao = db.songsDao()
    @Provides fun provideDraftDao(db: AppDatabase): DraftDao = db.draftsDao()
    @Provides fun provideEditDao(db: AppDatabase): EditDao = db.editsDao()
    @Provides fun provideAutoPlayDao(db: AppDatabase): AutoPlayDao = db.autoPlayDao()
}
