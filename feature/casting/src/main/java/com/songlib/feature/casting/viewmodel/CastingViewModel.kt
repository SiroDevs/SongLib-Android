package com.songlib.feature.casting.viewmodel

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import androidx.lifecycle.ViewModel
import com.songlib.core.casting.service.CastingForegroundService
import com.songlib.core.common.entity.CastingState
import com.songlib.core.common.entity.HotspotStatus
import com.songlib.core.common.entity.ServerStatus
import com.songlib.core.data.repos.CastingRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class CastingViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    repo: CastingRepo,
) : ViewModel() {

    val serverStatus: StateFlow<ServerStatus> = repo.serverStatus
    val slideState: StateFlow<CastingState> = repo.slideState
    val connectedClients: StateFlow<Int> = repo.connectedClients
    val hotspotStatus: StateFlow<HotspotStatus> = repo.hotspotStatus

    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    private val _wifiConnected = MutableStateFlow(isOnWifiNow())
    val wifiConnected: StateFlow<Boolean> = _wifiConnected.asStateFlow()

    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            _wifiConnected.value = isOnWifiNow()
        }

        override fun onLost(network: Network) {
            _wifiConnected.value = isOnWifiNow()
        }

        override fun onCapabilitiesChanged(network: Network, networkCapabilities: NetworkCapabilities) {
            _wifiConnected.value = isOnWifiNow()
        }
    }

    init {
        // Don't require validated internet — a LAN-only Wi-Fi connection still
        // counts as "on Wi-Fi" for casting purposes.
        val request = NetworkRequest.Builder()
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .removeCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()
        runCatching { connectivityManager.registerNetworkCallback(request, networkCallback) }
    }

    private fun isOnWifiNow(): Boolean {
        val network = connectivityManager.activeNetwork ?: return false
        val caps = connectivityManager.getNetworkCapabilities(network) ?: return false
        return caps.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
    }

    fun startCasting() {
        context.startForegroundService(CastingForegroundService.startIntent(context))
    }

    fun stopCasting() {
        context.startService(CastingForegroundService.stopIntent(context))
    }

    fun startHotspot() {
        context.startForegroundService(CastingForegroundService.startHotspotIntent(context))
    }

    fun stopHotspot() {
        context.startService(CastingForegroundService.stopHotspotIntent(context))
    }

    override fun onCleared() {
        runCatching { connectivityManager.unregisterNetworkCallback(networkCallback) }
        super.onCleared()
    }
}