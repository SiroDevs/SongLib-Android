package com.songlib.core.data.repos

import android.util.Log
import com.songlib.core.common.utils.ApiConstants
import com.songlib.core.network.dtos.PaystackCustomField
import com.songlib.core.network.dtos.PaystackInitializeRequest
import com.songlib.core.network.dtos.PaystackMetadata
import com.songlib.core.network.services.PaystackService
import com.songlib.core.data.BuildConfig
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.roundToLong

private const val TAG = "DonationRepo"

@Singleton
class DonationRepo @Inject constructor(
    private val paystackService: PaystackService
) {
    suspend fun submitDonation(
        amountUsd: Double,
        donorName: String? = null,
        donorEmail: String? = null,
    ): Result<String> {
        return try {
            val amountInCents = (amountUsd * 100).roundToLong()
            val reference = "SONGLIB-${UUID.randomUUID().toString().take(8).uppercase()}"

            val email = donorEmail?.takeIf { it.isNotBlank() } ?: ApiConstants.DONOR_EMAIL

            val customFields = buildList {
                add(PaystackCustomField(displayName = "App", variableName = "app", value = "SongLib"))
                add(PaystackCustomField(displayName = "Reference", variableName = "reference", value = reference))
                if (!donorName.isNullOrBlank()) {
                    add(PaystackCustomField(displayName = "Donor Name", variableName = "donor_name", value = donorName))
                }
            }

            val response = paystackService.initializeTransaction(
                bearer = "Bearer ${BuildConfig.PaystackSecretKey}",
                body = PaystackInitializeRequest(
                    email = email,
                    amount = amountInCents,
                    callbackUrl = ApiConstants.PAYSTACK_CALLBACK_URL,
                    metadata = PaystackMetadata(customFields = customFields),
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