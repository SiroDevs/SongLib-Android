package com.songlib.core.casting.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CastingRepo @Inject constructor() {

    private val _slideState = MutableStateFlow<CastingState>(CastingState.Idle)
    val slideState: StateFlow<CastingState> = _slideState.asStateFlow()

    private val _serverStatus = MutableStateFlow<ServerStatus>(ServerStatus.Stopped)
    val serverStatus: StateFlow<ServerStatus> = _serverStatus.asStateFlow()

    private val _connectedClients = MutableStateFlow(0)
    val connectedClients: StateFlow<Int> = _connectedClients.asStateFlow()

    /** Called when a song/draft is opened (or replaced) on a presenter screen. */
    fun publishSlide(
        source: String,
        title: String,
        verses: List<String>,
        indicators: List<String>,
        currentIndex: Int = 0,
    ) {
        if (verses.isEmpty()) {
            publishIdle()
            return
        }
        _slideState.value = CastingState.Slide(
            source = source,
            title = title,
            verses = verses,
            indicators = indicators,
            currentIndex = currentIndex.coerceIn(0, verses.size - 1),
        )
    }

    /** Called on every verse/page navigation while a presenter screen is open. */
    fun updateIndex(index: Int) {
        val current = _slideState.value
        if (current is CastingState.Slide && current.verses.isNotEmpty()) {
            val safeIndex = index.coerceIn(0, current.verses.size - 1)
            if (safeIndex != current.currentIndex) {
                _slideState.value = current.copy(currentIndex = safeIndex)
            }
        }
    }

    /** Called when the presenter screen is closed — falls back to the waiting page. */
    fun publishIdle() {
        _slideState.value = CastingState.Idle
    }

    fun setServerStatus(status: ServerStatus) {
        _serverStatus.value = status
    }

    fun onClientConnected() {
        _connectedClients.update { it + 1 }
    }

    fun onClientDisconnected() {
        _connectedClients.update { (it - 1).coerceAtLeast(0) }
    }
}