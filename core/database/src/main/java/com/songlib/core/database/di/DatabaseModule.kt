package com.songlib.core.database.di

import android.content.Context
import androidx.room.Room
import com.songlib.core.database.AppDatabase
import com.songlib.core.database.daos.HistoryDao
import com.songlib.core.database.daos.IdiomDao
import com.songlib.core.database.daos.ProverbDao
import com.songlib.core.database.daos.SayingDao
import com.songlib.core.database.daos.SearchDao
import com.songlib.core.database.daos.WordDao
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
        Room.databaseBuilder(appContext, AppDatabase::class.java, "SwahiliLibrary")
            .fallbackToDestructiveMigration()
            .build()

    @Provides fun provideHistoryDao(db: AppDatabase): HistoryDao = db.historiesDao()
    @Provides fun provideIdiomDao(db: AppDatabase): IdiomDao = db.idiomsDao()
    @Provides fun provideProverbDao(db: AppDatabase): ProverbDao = db.proverbsDao()
    @Provides fun provideSayingDao(db: AppDatabase): SayingDao = db.sayingsDao()
    @Provides fun provideSearchDao(db: AppDatabase): SearchDao = db.searchesDao()
    @Provides fun provideWordDao(db: AppDatabase): WordDao = db.wordsDao()
}
