package com.songlib.core.data.di

import android.content.Context
import com.songlib.core.network.ApiService
import com.songlib.core.network.di.NetworkModule
import com.songlib.core.data.repos.ListingRepo
import com.songlib.core.data.repos.PrefsRepo
import com.songlib.core.data.repos.SongBookRepo
import com.songlib.core.data.repos.SubsRepo
import com.songlib.core.data.repos.ThemeRepository
import com.songlib.core.data.repos.TrackingRepo
import dagger.*
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module(includes = [NetworkModule::class])
object DataModule {

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
