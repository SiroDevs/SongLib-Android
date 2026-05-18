package com.songlib.core.data.di

import android.content.Context
import com.songlib.core.network.ApiService
import com.songlib.core.network.di.NetworkModule
import com.songlib.core.data.repos.ListingRepo
import com.songlib.core.data.repos.PrefsRepo
import com.songlib.core.data.repos.SongBookRepo
import com.songlib.core.data.repos.ThemeRepo
import com.songlib.core.data.repos.TrackingRepo
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
@Module(includes = [NetworkModule::class])
object DataModule {
    @Provides
    @Singleton
    fun provideListingRepo(
        listingsDao: ListingDao
    ): ListingRepo = ListingRepo(listingsDao)

    @Provides
    @Singleton
    fun providePreferencesRepo(
        @ApplicationContext context: Context,
    ): PrefsRepo = PrefsRepo(context)

    @Provides
    @Singleton
    fun provideSongBookRepo(
        apiService: ApiService,
        booksDao: BookDao,
        songsDao: SongDao,
    ): SongBookRepo = SongBookRepo(apiService, booksDao, songsDao)

    @Provides
    @Singleton
    fun provideThemeRepo(
        prefsRepo: PrefsRepo,
    ): ThemeRepo = ThemeRepo(prefsRepo)

    @Provides
    @Singleton
    fun provideTrackingRepo(
        historiesDao: HistoryDao,
        searchesDao: SearchDao
    ): TrackingRepo = TrackingRepo(historiesDao, searchesDao)
}
