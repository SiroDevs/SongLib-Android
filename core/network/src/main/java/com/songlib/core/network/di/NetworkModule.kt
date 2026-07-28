package com.songlib.core.network.di

import com.songlib.core.network.services.PaystackService
import com.songlib.core.network.services.SongLibService
import com.songlib.core.common.utils.ApiConstants
import com.songlib.core.network.BuildConfig
import dagger.Module
import dagger.Provides
import dagger.Reusable
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
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
    fun provideOkHttpClient(): OkHttpClient {
        val apiKeyInterceptor = Interceptor { chain ->
            val request = chain.request().newBuilder()
                .addHeader("x-api-key", BuildConfig.SonglibApiKey)
                .build()
            chain.proceed(request)
        }

        return OkHttpClient.Builder()
            .addInterceptor(apiKeyInterceptor)
            .addInterceptor(
                HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.BODY
                }
            )
            .build()
    }

    @Provides
    @Reusable
    fun provideSonglibApiService(
        @Named("songlibApi") retrofit: Retrofit
    ): SongLibService {
        return retrofit.create(SongLibService::class.java)
    }

    @Provides
    @Named("songlibApi")
    @Reusable
    fun provideSonglibApi(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(ApiConstants.SONGLIB_BASE)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
    }

    @Provides
    @Reusable
    fun providePaystackApiService(
        @Named("paystackApi") retrofit: Retrofit
    ): PaystackService {
        return retrofit.create(PaystackService::class.java)
    }

    @Provides
    @Named("paystackApi")
    @Reusable
    fun providePaystackApi(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(ApiConstants.PAYSTACK_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
    }
}
