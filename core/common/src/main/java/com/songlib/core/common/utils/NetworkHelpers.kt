package com.songlib.core.common.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.net.Inet4Address
import java.net.NetworkInterface
import java.util.Collections

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

    fun getLocalIpAddresses(): List<String> {
        val addresses = mutableListOf<String>()
        try {
            val interfaces = Collections.list(NetworkInterface.getNetworkInterfaces())
            for (iface in interfaces) {
                if (!iface.isUp || iface.isLoopback) continue
                if (isCellularInterface(iface.name)) continue
                val ifaceAddresses = Collections.list(iface.inetAddresses)
                for (addr in ifaceAddresses) {
                    if (addr is Inet4Address && !addr.isLoopbackAddress) {
                        addr.hostAddress?.let { addresses.add(it) }
                    }
                }
            }
        } catch (_: Exception) {
            // Best-effort — an empty list just means we show no links yet.
        }
        return addresses.distinct().sortedByDescending(::looksLikeHotspotAddress)
    }

    fun getPrimaryLocalIpAddress(): String? = getLocalIpAddresses().firstOrNull()

    fun hasHotspotIpAddress(): Boolean =
        getLocalIpAddresses().any(::looksLikeHotspotAddress)

    private fun looksLikeHotspotAddress(address: String): Boolean =
        address.startsWith("192.168.43.") || address.startsWith("192.168.49.")

    private fun isCellularInterface(name: String?): Boolean {
        if (name == null) return false
        return name.startsWith("rmnet") ||
                name.startsWith("ccmni") ||
                name.startsWith("pdp_ip") ||
                name.startsWith("rev_rmnet") ||
                name.startsWith("ecm")
    }
}
