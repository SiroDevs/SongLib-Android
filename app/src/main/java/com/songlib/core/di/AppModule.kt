package com.songlib.core.di

import android.content.Context
import com.songlib.data.sources.remote.ApiService
import com.songlib.domain.repository.*
import dagger.*
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module(includes = [NetworkModule::class])
class AppModule {
    @Provides
    @Singleton
    fun provideListingRepository(
        @ApplicationContext context: Context,
    ): ListingRepository = ListingRepository(context)

    @Provides
    @Singleton
    fun providePreferencesRepository(
        @ApplicationContext context: Context,
    ): PreferencesRepository = PreferencesRepository(context)

    @Provides
    @Singleton
    fun provideSongBookRepository(
        @ApplicationContext context: Context,
        apiService: ApiService,
    ): SongBookRepository = SongBookRepository(context, apiService)

    @Provides
    @Singleton
    fun provideThemeRepository(
        prefsRepo: PreferencesRepository,
    ): ThemeRepository = ThemeRepository(prefsRepo)

    @Provides
    @Singleton
    fun provideTrackingRepository(
        @ApplicationContext context: Context,
    ): TrackingRepository = TrackingRepository(context)

}