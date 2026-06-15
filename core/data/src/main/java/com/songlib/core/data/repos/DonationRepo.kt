package com.songlib.core.data.repos

import android.util.Log
import com.songlib.core.common.utils.ApiConstants
import com.songlib.core.network.dtos.PesaPalAuthRequest
import com.songlib.core.network.dtos.PesaPalBillingAddress
import com.songlib.core.network.dtos.PesaPalOrderRequest
import com.songlib.core.network.services.PesaPalService
import java.util.UUID
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

private const val TAG = "DonationRepo"

@Singleton
class DonationRepo @Inject constructor(
    private val pesapalService: PesaPalService,
    @Named("pesapal_consumer_key") private val consumerKey: String,
    @Named("pesapal_consumer_secret") private val consumerSecret: String,
    @Named("pesapal_ipn_id") private val ipnId: String,
) {
    suspend fun submitDonation(
        amountUsd: Double
    ): Result<String> {
        return try {
            val authResponse = pesapalService.requestPesapalAuthToken(
                PesaPalAuthRequest(
                    consumerKey = consumerKey,
                    consumerSecret = consumerSecret,
                )
            )
            val token = authResponse.token
            if (token.isNullOrBlank()) {
                Log.e(TAG, "❌ Auth failed: ${authResponse.message}")
                return Result.failure(Exception("Unable to get permission for payment"))
            }
            Log.d(TAG, "✅ Auth token obtained")

            val merchantRef = "SONGLIB-${UUID.randomUUID().toString().take(8).uppercase()}"
            val orderResponse = pesapalService.submitOrderToPesapal(
                bearer = "Bearer $token",
                body = PesaPalOrderRequest(
                    id = merchantRef,
                    currency = "USD",
                    amount = amountUsd,
                    description = "Donation for SongLib — Thank you!",
                    callbackUrl = ApiConstants.CALLBACK_URL,
                    notificationId = ipnId,
                    billingAddress = PesaPalBillingAddress(emailAddress = ApiConstants.DONOR_EMAIL),
                ),
            )
            val redirectUrl = orderResponse.redirectUrl
            if (redirectUrl.isNullOrBlank()) {
                Log.e(TAG, "❌ Order submission failed: ${orderResponse.message}")
                return Result.failure(Exception("Unable to request payment"))
            }

            Log.d(TAG, "✅ Order accepted — redirect URL: $redirectUrl")
            Result.success(redirectUrl)
        } catch (e: Exception) {
            Log.e(TAG, "❌ Donation error: ${e.message}", e)
            Result.failure(e)
        }
    }
}
