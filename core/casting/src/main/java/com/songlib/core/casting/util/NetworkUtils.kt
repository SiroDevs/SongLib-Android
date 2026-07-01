package com.songlib.core.casting.util

import java.net.Inet4Address
import java.net.NetworkInterface
import java.util.Collections

object NetworkUtils {

    /**
     * Returns the local IPv4 addresses this device currently holds — e.g. the
     * address handed out on a personal-hotspot interface (commonly something
     * like 192.168.43.1 or 192.168.49.1 on Android), or whatever a shared Wi-Fi
     * router assigned it. Loopback and down interfaces are skipped.
     *
     * Hotspot-looking addresses are sorted first since that's the primary use
     * case ("turn on your hotspot, connect your PC to it, open this link").
     */
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

    /**
     * A single best-guess local IPv4 address to show as "the" casting link —
     * hotspot-looking addresses win, otherwise whatever was found first.
     * Returning one address instead of a list keeps the UI from showing several
     * links that are confusing to a non-technical user.
     */
    fun getPrimaryLocalIpAddress(): String? = getLocalIpAddresses().firstOrNull()

    private fun looksLikeHotspotAddress(address: String): Boolean =
        address.startsWith("192.168.43.") || address.startsWith("192.168.49.")

    /**
     * Cellular interfaces (rmnet_*, ccmni*, pdp_ip*, etc.) carry mobile-data
     * traffic and are never reachable from a Wi-Fi/hotspot peer, so we skip
     * them — otherwise the URL would advertise a carrier-NATed address that
     * no PC on the local network can reach.
     */
    private fun isCellularInterface(name: String?): Boolean {
        if (name == null) return false
        return name.startsWith("rmnet") ||
                name.startsWith("ccmni") ||
                name.startsWith("pdp_ip") ||
                name.startsWith("rev_rmnet") ||
                name.startsWith("ecm")
    }
}
