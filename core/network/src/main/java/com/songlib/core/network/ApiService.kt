package com.songlib.core.network

import androidx.annotation.Keep
import com.songlib.core.common.utils.ApiConstants
import com.songlib.core.database.model.*
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
