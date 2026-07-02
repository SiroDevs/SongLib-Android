package com.songlib.feature.casting.viewmodel

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.songlib.core.casting.service.CastingForegroundService
import com.songlib.core.casting.util.NetworkUtils
import com.songlib.core.common.entity.CastingState
import com.songlib.core.common.entity.HotspotStatus
import com.songlib.core.common.entity.ServerStatus
import com.songlib.core.data.repos.CastingRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CastingViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val repo: CastingRepo,
) : ViewModel() {

    val serverStatus: StateFlow<ServerStatus> = repo.serverStatus
    val slideState: StateFlow<CastingState> = repo.slideState
    val connectedClients: StateFlow<Int> = repo.connectedClients
    val hotspotStatus: StateFlow<HotspotStatus> = repo.hotspotStatus

    private val connManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    private val _wifiConnected = MutableStateFlow(isOnWifiNow())
    val wifiConnected: StateFlow<Boolean> = _wifiConnected.asStateFlow()

    private val _hasExternalHotspot = MutableStateFlow(computeExternalHotspot())
    val hasExternalHotspot: StateFlow<Boolean> = _hasExternalHotspot.asStateFlow()

    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            refreshNetworkState()
        }

        override fun onLost(network: Network) {
            refreshNetworkState()
        }

        override fun onCapabilitiesChanged(
            network: Network,
            networkCapabilities: NetworkCapabilities
        ) {
            refreshNetworkState()
        }
    }

    init {
        val request = NetworkRequest.Builder()
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .removeCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()
        runCatching { connManager.registerNetworkCallback(request, networkCallback) }

        // Our own hotspot toggles the external-hotspot signal too — recompute
        // whenever the app-managed hotspot state changes so the "OS hotspot is
        // already up" heuristic stays correct.
        viewModelScope.launch {
            repo.hotspotStatus.collect { refreshNetworkState() }
        }
    }

    private fun refreshNetworkState() {
        _wifiConnected.value = isOnWifiNow()
        _hasExternalHotspot.value = computeExternalHotspot()
        maybeStopIfStranded()
    }

    /**
     * If the server is running but every path a peer could reach us on has
     * dropped (no Wi-Fi, no external hotspot, and our own hotspot isn't up
     * either), stop the service — there's nothing left to broadcast on and
     * leaving the notification up is misleading.
     */
    private fun maybeStopIfStranded() {
        val server = repo.serverStatus.value
        val casting = server is ServerStatus.Running || server is ServerStatus.Starting
        if (!casting) return
        val ourHotspotUp = repo.hotspotStatus.value is HotspotStatus.Running
        val anyNetwork = _wifiConnected.value || _hasExternalHotspot.value || ourHotspotUp
        if (!anyNetwork) {
            stopCasting()
        }
    }

    private fun isOnWifiNow(): Boolean {
        val network = connManager.activeNetwork ?: return false
        val caps = connManager.getNetworkCapabilities(network) ?: return false
        return caps.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
    }

    private fun computeExternalHotspot(): Boolean {
        // Only "external" if we didn't create it ourselves — otherwise our own
        // 192.168.49.* interface would masquerade as an OS hotspot and hide
        // the Start/Stop controls of the app-managed one.
        val ourHotspotRunning = repo.hotspotStatus.value is HotspotStatus.Running
        return !ourHotspotRunning && NetworkUtils.hasHotspotIpAddress()
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
        runCatching { connManager.unregisterNetworkCallback(networkCallback) }
        super.onCleared()
    }
}