package com.songlib.core.di

//import com.songlib.BuildConfig
import com.songlib.core.utils.ApiConstants
import com.songlib.data.sources.remote.ApiService
import dagger.Module
import dagger.Provides
import dagger.Reusable
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
    @JvmStatic
    internal fun provideApiService(
        @Named("songlibApi") retrofit: Retrofit
    ): ApiService {
        return retrofit.create(ApiService::class.java)
    }

    @Provides
    @Named("songlibApi")
    @Reusable
    @JvmStatic
    internal fun provideSonglibApi(
        okHttpClient: OkHttpClient.Builder,
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(ApiConstants.BASE)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient.build())
            .build()
    }

    @Provides
    @Reusable
    @JvmStatic
    internal fun provideOkHttp(): OkHttpClient.Builder {
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY

        val okHttpClient = OkHttpClient.Builder()
        //if (BuildConfig.DEBUG) {
            okHttpClient.addInterceptor(logging)
        //}
        return okHttpClient
    }
}