package com.songlib.core.network

import androidx.annotation.Keep
import com.songlib.core.common.utils.ApiConstants
import com.songlib.core.database.model.BookEntity
import com.songlib.core.database.model.SongEntity
import retrofit2.http.GET
import retrofit2.http.Path

@Keep
interface ApiService {
    @GET(ApiConstants.BOOKS)
    suspend fun getBooks(): List<BookEntity>

    @GET("${ApiConstants.SONGS}/books/{bookIds}")
    suspend fun getSongs(
        @Path("bookIds") bookIds: String
    ): List<SongEntity>
}
