package com.songlib.core.casting.hotspot

import android.content.Context
import android.net.wifi.SoftApConfiguration
import android.net.wifi.WifiManager
import android.os.Build
import kotlin.random.Random

/** What the platform actually gave us once the hotspot came up. */
data class HotspotInfo(
    val ssid: String,
    val password: String?,
    val isOpen: Boolean,
)

sealed interface HotspotOutcome {
    data class Success(val info: HotspotInfo) : HotspotOutcome
    data class Failure(val message: String) : HotspotOutcome
}

/**
 * Wraps Android's LocalOnlyHotspot API to create a self-contained Wi-Fi access
 * point for casting. This is deliberately *not* the device's system Personal
 * Hotspot (Tethering) — a 3rd-party app has no public API to read or toggle
 * that. LocalOnlyHotspot is a separate, app-scoped AP mode that:
 *
 * - Never bridges connected devices to the phone's mobile data or any other
 *   uplink (that's the whole point of "local only" — it's not tethering).
 * - Is fully owned by this app: starting a new one always tears down whatever
 *   reservation we were already holding, so every "Start Hotspot" tap gives a
 *   genuinely fresh AP rather than silently reusing a stale one.
 * - Can only have its SSID/security fully customized on Android 13+. On older
 *   versions the platform assigns a random SSID/passphrase that we read back
 *   and surface as-is — there's no public API to rename or open it pre-13.
 */
class HotspotController(context: Context) {

    private val appContext = context.applicationContext
    private val wifiManager = appContext.getSystemService(Context.WIFI_SERVICE) as WifiManager

    private var reservation: WifiManager.LocalOnlyHotspotReservation? = null

    val isActive: Boolean get() = reservation != null

    fun start(onResult: (HotspotOutcome) -> Unit) {
        // Requirement: every start should hand back a fresh hotspot.
        stop()

        val requestedSsid = generateSsid()

        val callback = object : WifiManager.LocalOnlyHotspotCallback() {
            override fun onStarted(res: WifiManager.LocalOnlyHotspotReservation) {
                reservation = res
                onResult(HotspotOutcome.Success(resolveInfo(res, requestedSsid)))
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
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                // Custom SSID + open security only available from Android 13 on.
                val config = SoftApConfiguration.Builder()
                    .setSsid(requestedSsid)
                    .setPassphrase(null, SoftApConfiguration.SECURITY_TYPE_OPEN)
                    .build()
                wifiManager.startLocalOnlyHotspot(config, appContext.mainExecutor, callback)
            } else {
                @Suppress("DEPRECATION")
                wifiManager.startLocalOnlyHotspot(callback, null)
            }
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
        requestedSsid: String,
    ): HotspotInfo {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // We asked for this exact open, named network ourselves.
            return HotspotInfo(ssid = requestedSsid, password = null, isOpen = true)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val config = res.softApConfiguration
            if (config != null) {
                val isOpen = config.securityType == SoftApConfiguration.SECURITY_TYPE_OPEN
                return HotspotInfo(
                    ssid = config.ssid ?: requestedSsid,
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
            ssid = legacySsid ?: requestedSsid,
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
