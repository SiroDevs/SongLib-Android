package com.songlib.data.sources.remote

import androidx.annotation.Keep
import com.songlib.core.utils.ApiConstants
import com.songlib.data.models.*
import retrofit2.http.*

@Keep
interface ApiService {
    @GET(ApiConstants.BOOKS)
    suspend fun getBooks(): List<Book>

    @GET(ApiConstants.SONGS)
    suspend fun getSongs(
        @Query("books") books: String,
    ): List<Song>
}
