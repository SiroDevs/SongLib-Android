package com.songlib.app.di

import com.songlib.BuildConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Named

@Module
@InstallIn(SingletonComponent::class)
object AppConfigModule {
    @Provides
    @Named("songlib_api_key")
    fun provideSonglibApiKey(): String = BuildConfig.SonglibApiKey

    @Provides
    @Named("paystack_secret_key")
    fun providePaystackSecretKey(): String = BuildConfig.PaystackSecretKey
}