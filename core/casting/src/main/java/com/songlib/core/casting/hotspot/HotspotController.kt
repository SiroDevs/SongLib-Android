package com.songlib.core.casting.hotspot

import android.content.Context
import android.net.wifi.SoftApConfiguration
import android.net.wifi.WifiManager
import android.os.Build
import kotlin.random.Random

data class HotspotInfo(
    val ssid: String,
    val password: String?,
    val isOpen: Boolean,
)

sealed interface HotspotOutcome {
    data class Success(val info: HotspotInfo) : HotspotOutcome
    data class Failure(val message: String) : HotspotOutcome
}

class HotspotController(context: Context) {

    private val appContext = context.applicationContext
    private val wifiManager = appContext.getSystemService(Context.WIFI_SERVICE) as WifiManager

    private var reservation: WifiManager.LocalOnlyHotspotReservation? = null

    val isActive: Boolean get() = reservation != null

    fun start(onResult: (HotspotOutcome) -> Unit) {
        stop()

        val fallbackSsid = generateSsid()

        val callback = object : WifiManager.LocalOnlyHotspotCallback() {
            override fun onStarted(res: WifiManager.LocalOnlyHotspotReservation) {
                reservation = res
                onResult(HotspotOutcome.Success(resolveInfo(res, fallbackSsid)))
            }

            override fun onStopped() {
                reservation = null
            }

            override fun onFailed(reason: Int) {
                reservation = null
                onResult(HotspotOutcome.Failure(failureMessage(reason)))
            }
        }

        try {
            @Suppress("DEPRECATION")
            wifiManager.startLocalOnlyHotspot(callback, null)
        } catch (e: Exception) {
            onResult(HotspotOutcome.Failure(e.message ?: "Couldn't start the hotspot"))
        }
    }

    fun stop() {
        reservation?.close()
        reservation = null
    }

    private fun generateSsid(): String = "Songlib Casting-${Random.nextInt(1000, 9999)}"

    private fun resolveInfo(
        res: WifiManager.LocalOnlyHotspotReservation,
        fallbackSsid: String,
    ): HotspotInfo {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val config = res.softApConfiguration
            if (config != null) {
                val isOpen = config.securityType == SoftApConfiguration.SECURITY_TYPE_OPEN
                return HotspotInfo(
                    ssid = config.ssid ?: fallbackSsid,
                    password = if (isOpen) null else config.passphrase,
                    isOpen = isOpen,
                )
            }
        }

        @Suppress("DEPRECATION")
        val legacy = res.wifiConfiguration
        @Suppress("DEPRECATION")
        val legacySsid = legacy?.SSID?.trim('"')
        @Suppress("DEPRECATION")
        val legacyPassword = legacy?.preSharedKey?.trim('"')
        return HotspotInfo(
            ssid = legacySsid ?: fallbackSsid,
            password = legacyPassword,
            isOpen = legacyPassword.isNullOrEmpty(),
        )
    }

    private fun failureMessage(reason: Int): String = when (reason) {
        WifiManager.LocalOnlyHotspotCallback.ERROR_NO_CHANNEL -> "No Wi-Fi channel is available right now"
        WifiManager.LocalOnlyHotspotCallback.ERROR_GENERIC -> "The hotspot couldn't be started"
        WifiManager.LocalOnlyHotspotCallback.ERROR_INCOMPATIBLE_MODE ->
            "Wi-Fi is busy with another connection, like tethering"
        WifiManager.LocalOnlyHotspotCallback.ERROR_TETHERING_DISALLOWED ->
            "Hotspot use is disabled on this device by policy"
        else -> "The hotspot couldn't be started (code $reason)"
    }
}