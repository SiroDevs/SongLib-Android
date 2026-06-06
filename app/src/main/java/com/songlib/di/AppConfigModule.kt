package com.songlib.di

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
    @Named("pesapal_consumer_key")
    fun providePesapalConsumerKey(): String = BuildConfig.PesapalConsumerKey

    @Provides
    @Named("pesapal_consumer_secret")
    fun providePesapalConsumerSectret(): String = BuildConfig.PesapalConsumerSecret

    @Provides
    @Named("pesapal_ipn_id")
    fun providePesapalIpnId(): String = BuildConfig.PesapalIpnId
}
