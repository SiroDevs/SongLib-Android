package com.songlib.core.data.repos

import android.util.Log
import com.songlib.core.common.utils.ApiConstants
import com.songlib.core.network.dtos.PaystackCustomField
import com.songlib.core.network.dtos.PaystackInitializeRequest
import com.songlib.core.network.dtos.PaystackMetadata
import com.songlib.core.network.services.PaystackService
import java.util.UUID
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton
import kotlin.math.roundToLong

private const val TAG = "DonationRepo"

@Singleton
class DonationRepo @Inject constructor(
    private val paystackService: PaystackService,
    @Named("paystack_secret_key") private val secretKey: String,
) {
    /**
     * Initializes a Paystack transaction and returns the authorization_url
     * which is then loaded in a WebView for the user to complete payment.
     *
     * @param amountUsd Donation amount in USD
     * @return Result containing the Paystack authorization URL on success
     */
    suspend fun submitDonation(amountUsd: Double): Result<String> {
        return try {
            // Paystack amounts are in the smallest currency unit (cents for USD)
            val amountInCents = (amountUsd * 100).roundToLong()
            val reference = "SONGLIB-${UUID.randomUUID().toString().take(8).uppercase()}"

            val response = paystackService.initializeTransaction(
                bearer = "Bearer $secretKey",
                body = PaystackInitializeRequest(
                    email = ApiConstants.DONOR_EMAIL,
                    amount = amountInCents,
                    currency = "USD",
                    callbackUrl = ApiConstants.PAYSTACK_CALLBACK_URL,
                    metadata = PaystackMetadata(
                        customFields = listOf(
                            PaystackCustomField(
                                displayName = "App",
                                variableName = "app",
                                value = "SongLib",
                            ),
                            PaystackCustomField(
                                displayName = "Reference",
                                variableName = "reference",
                                value = reference,
                            ),
                        )
                    ),
                ),
            )

            val authUrl = response.data?.authorizationUrl
            if (!response.status || authUrl.isNullOrBlank()) {
                Log.e(TAG, "❌ Paystack init failed: ${response.message}")
                return Result.failure(Exception(response.message ?: "Unable to initialize payment"))
            }

            Log.d(TAG, "✅ Paystack transaction initialized — URL: $authUrl")
            Result.success(authUrl)
        } catch (e: Exception) {
            Log.e(TAG, "❌ Donation error: ${e.message}", e)
            Result.failure(e)
        }
    }
}
