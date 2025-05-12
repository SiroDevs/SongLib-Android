package com.songlib.core.di

import com.google.gson.GsonBuilder
import com.songlib.BuildConfig
import com.songlib.core.utils.ApiConstants
import com.songlib.data.sources.remote.ApiService
import dagger.*
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named

@InstallIn(SingletonComponent::class)
@Module
@Suppress("unused")
object NetworkModule {
    @Provides
    @Reusable
    fun provideApiService(@Named("songlibApi") retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }

    @Provides
    @Named("songlibApi")
    @Reusable
    fun provideSonglibApi(okHttpClient: OkHttpClient): Retrofit {
        val gson = GsonBuilder().setLenient().create()

        return Retrofit.Builder()
            .baseUrl(ApiConstants.BASE)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(okHttpClient)
            .build()
    }

    @Provides
    @Reusable
    fun provideOkHttp(): OkHttpClient {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        return OkHttpClient.Builder()
            .apply {
                if (BuildConfig.DEBUG) {
                    addInterceptor(logging)
                }
            }
            .build()
    }
}