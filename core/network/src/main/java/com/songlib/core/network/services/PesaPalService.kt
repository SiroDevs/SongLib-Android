package com.songlib.core.network.services

import androidx.annotation.Keep
import com.songlib.core.common.utils.ApiConstants
import com.songlib.core.network.dtos.PesaPalAuthRequest
import com.songlib.core.network.dtos.PesaPalAuthResponse
import com.songlib.core.network.dtos.PesaPalOrderRequest
import com.songlib.core.network.dtos.PesaPalOrderResponse
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

@Keep
interface PesaPalService {
    @POST(ApiConstants.PESAPAL_AUTH)
    suspend fun requestPesapalAuthToken(
        @Body body: PesaPalAuthRequest,
    ): PesaPalAuthResponse

    @POST(ApiConstants.PESAPAL_ORDER)
    suspend fun submitOrderToPesapal(
        @Header("Authorization") bearer: String,
        @Body body: PesaPalOrderRequest,
    ): PesaPalOrderResponse
}