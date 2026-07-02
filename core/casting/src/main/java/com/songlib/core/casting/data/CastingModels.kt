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
        val source: String,
        val title: String,
        val book: String? = null,
        val verses: List<String>,
        val indicators: List<String>,
        val currentIndex: Int,
    ) : CastingState
}

sealed interface ServerStatus {
    data object Stopped : ServerStatus
    data object Starting : ServerStatus
    data class Running(val urls: List<String>, val port: Int) : ServerStatus
    data class Error(val message: String) : ServerStatus
}
