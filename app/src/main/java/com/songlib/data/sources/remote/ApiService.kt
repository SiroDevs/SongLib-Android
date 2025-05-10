package com.songlib.data.sources.remote

import androidx.annotation.Keep
import com.songlib.core.utils.ApiConstants
import com.songlib.data.models.*
import retrofit2.http.*

@Keep
interface ApiService {
    @GET(ApiConstants.BOOKS)
    suspend fun getBooks(): BookResp

    @GET("${ApiConstants.SONGS}{booksId}")
    suspend fun getSongsByBook(
        @Path("booksId") booksId: String,
    ): SongResp
}
