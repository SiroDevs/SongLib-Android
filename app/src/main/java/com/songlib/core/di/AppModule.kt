package com.songlib.core.di

import android.content.Context
import com.songlib.data.sources.remote.ApiService
import com.songlib.domain.repos.ListingRepo
import com.songlib.domain.repos.PrefsRepo
import com.songlib.domain.repos.SongBookRepo
import com.songlib.domain.repos.SubsRepo
import com.songlib.domain.repos.ThemeRepository
import com.songlib.domain.repos.TrackingRepo
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
    ): ListingRepo = ListingRepo(context)

    @Provides
    @Singleton
    fun providePreferencesRepository(
        @ApplicationContext context: Context,
    ): PrefsRepo = PrefsRepo(context)

    @Provides
    @Singleton
    fun provideSongBookRepository(
        @ApplicationContext context: Context,
        apiService: ApiService,
    ): SongBookRepo = SongBookRepo(context, apiService)

    @Provides
    @Singleton
    fun provideSubscriptionRepository(
    ): SubsRepo = SubsRepo()

    @Provides
    @Singleton
    fun provideThemeRepository(
        prefsRepo: PrefsRepo,
    ): ThemeRepository = ThemeRepository(prefsRepo)

    @Provides
    @Singleton
    fun provideTrackingRepository(
        @ApplicationContext context: Context,
    ): TrackingRepo = TrackingRepo(context)
}