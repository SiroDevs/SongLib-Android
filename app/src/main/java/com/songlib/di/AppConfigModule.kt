package com.songlib.di

//import com.songlib.BuildConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Named

@Module
@InstallIn(SingletonComponent::class)
object AppConfigModule {
//    @Provides
//    @Named("supabase_url")
//    fun provideSupabaseUrl(): String = BuildConfig.SupabaseUrl
//
//    @Provides
//    @Named("supabase_key")
//    fun provideSupabaseKey(): String = BuildConfig.SupabaseKey
}
