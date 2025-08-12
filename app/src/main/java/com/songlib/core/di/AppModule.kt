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
    fun provideBookRepository(
        @ApplicationContext context: Context,
        apiService: ApiService,
    ): BookRepository = BookRepository(context, apiService)

    @Provides
    @Singleton
    fun providePrefsRepository(
        @ApplicationContext context: Context,
    ): PrefsRepository = PrefsRepository(context)

    @Provides
    @Singleton
    fun provideSongRepository(
        @ApplicationContext context: Context,
        apiService: ApiService,
    ): SongRepository = SongRepository(context, apiService)

    @Provides
    @Singleton
    fun provideThemeRepository(
        prefsRepo: PrefsRepository,
    ): ThemeRepository = ThemeRepository(prefsRepo)

}