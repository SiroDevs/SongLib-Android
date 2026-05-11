package com.songlib.data.sources.remote

import androidx.annotation.Keep
import com.songlib.core.utils.ApiConstants
import com.songlib.data.models.*
import retrofit2.http.*

@Keep
interface ApiService {
    @GET(ApiConstants.BOOKS)
    suspend fun getBooks(): List<Book>

    @GET("${ApiConstants.SONGS}/books/{bookIds}")
    suspend fun getSongs(
        @Path("bookIds") bookIds: String
    ): List<Song>
}
