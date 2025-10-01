package com.songlib.domain.repository

import com.revenuecat.purchases.*
import com.songlib.core.utils.AppConstants
import javax.inject.*

@Singleton
class SubscriptionsRepository @Inject constructor() {
    fun isActiveSubscriber(completion: (Boolean) -> Unit) {
        Purchases.sharedInstance.getCustomerInfoWith(
            fetchPolicy = CacheFetchPolicy.FETCH_CURRENT,
            onError = { error ->
                completion(false)
            },
            onSuccess = { customerInfo ->
                val isActive = customerInfo.entitlements[AppConstants.ENTITLEMENTS]?.isActive == true
                completion(isActive)
            }
        )
    }
}