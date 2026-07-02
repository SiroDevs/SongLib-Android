package com.songlib.core.casting.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed interface CastingState {

    @Serializable
    @SerialName("idle")
    data object Idle : CastingState

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

sealed interface ServerStatus {
    data object Stopped : ServerStatus
    data object Starting : ServerStatus
    data class Running(val url: String?, val port: Int) : ServerStatus
    data class Error(val message: String) : ServerStatus
}

sealed interface HotspotStatus {
    data object Stopped : HotspotStatus
    data object Starting : HotspotStatus
    data class Running(val ssid: String, val password: String?, val isOpen: Boolean) : HotspotStatus
    data class Error(val message: String) : HotspotStatus
}

