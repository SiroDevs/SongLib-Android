package com.songlib.core.network.services

import androidx.annotation.Keep
import com.songlib.core.common.utils.ApiConstants
import com.songlib.core.database.model.BookEntity
import com.songlib.core.database.model.SongEntity
import com.songlib.core.network.dtos.PesaPalAuthRequest
import com.songlib.core.network.dtos.PesaPalAuthResponse
import com.songlib.core.network.dtos.PesaPalOrderRequest
import com.songlib.core.network.dtos.PesaPalOrderResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

@Keep
interface SongLibService {
    @GET(ApiConstants.SONGLIB_BOOKS)
    suspend fun getBooks(): List<BookEntity>

    @GET("${ApiConstants.SONGLIB_SONGS}/books/{bookIds}")
    suspend fun getSongs(
        @Path("bookIds") bookIds: String
    ): List<SongEntity>
}