package com.songlib.core.common.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Everything a connected browser needs to render the current slide.
 * This is the exact payload pushed down the WebSocket — kept intentionally
 * small and self-contained so the web page never has to ask twice.
 */
@Serializable
sealed interface CastingState {

    /** Nothing is being presented right now — show the "waiting" page. */
    @Serializable
    @SerialName("idle")
    data object Idle : CastingState

    /** A song or draft is open on the presenter screen. */
    @Serializable
    @SerialName("slide")
    data class Slide(
        val source: String, // "song" or "draft" — purely informational for the web client
        val title: String,
        val book: String? = null, // songbook name (songs) or a label like "Draft" (drafts)
        val verses: List<String>,
        val indicators: List<String>,
        val currentIndex: Int,
    ) : CastingState
}

/** Local-only status of the embedded server — never sent over the wire. */
sealed interface ServerStatus {
    data object Stopped : ServerStatus
    data object Starting : ServerStatus
    data class Running(val url: String?, val port: Int) : ServerStatus
    data class Error(val message: String) : ServerStatus
}

/**
 * Status of the app-managed Wi-Fi hotspot (Android's LocalOnlyHotspot) used for
 * casting — distinct from, and unrelated to, the device's system Personal
 * Hotspot. Never sent over the wire; purely local UI/service state.
 */
sealed interface HotspotStatus {
    data object Stopped : HotspotStatus
    data object Starting : HotspotStatus
    data class Running(val ssid: String, val password: String?, val isOpen: Boolean) : HotspotStatus
    data class Error(val message: String) : HotspotStatus
}
