package com.songlib.core.helpers

import android.content.Context
import android.net.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*

sealed interface NetworkResult<out T> {
    data class Failure(val exception: Exception) : NetworkResult<Nothing>

    data class Success<T>(val data: T) : NetworkResult<T>
}

object NetworkUtils {
    fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork
        val networkCapabilities = connectivityManager.getNetworkCapabilities(network)

        return networkCapabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
    }

    fun observeNetworkConnectivity(context: Context): Flow<Boolean> = callbackFlow {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                trySend(true)
            }

            override fun onLost(network: Network) {
                trySend(false)
            }
        }

        val networkRequest = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()

        connectivityManager.registerNetworkCallback(networkRequest, networkCallback)

        // Send initial state
        trySend(isNetworkAvailable(context))

        awaitClose {
            connectivityManager.unregisterNetworkCallback(networkCallback)
        }
    }
}
