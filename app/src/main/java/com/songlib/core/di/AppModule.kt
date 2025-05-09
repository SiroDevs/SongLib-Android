package com.songlib.core.di

import android.content.Context
import com.songlib.data.sources.remote.ApiService
import com.songlib.domain.repositories.SelectionRepository
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
    fun provideSelectionRepository(
        @ApplicationContext context: Context,
        apiService: ApiService
    ): SelectionRepository = SelectionRepository(context, apiService)
}