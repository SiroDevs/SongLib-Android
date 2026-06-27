package com.songlib.core.broadcast

import com.songlib.core.broadcast.model.BroadcastState
import com.songlib.core.broadcast.model.ServerStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Single source of truth for the "Xender-like" local broadcast feature.
 *
 * The Presenter and Draft-presenter view models call [publishSlide] / [updateIndex] /
 * [publishIdle] as the user opens songs and flips through verses. The embedded
 * web server (see [com.songlib.core.broadcast.server.BroadcastHttpServer]) simply
 * observes [slideState] and forwards every emission to connected browsers — it
 * never needs to know *why* the state changed, only *what* it currently is.
 *
 * Kept as a plain singleton (like the other *Repo classes in this codebase) so
 * it works whether or not the broadcast server is currently running: presenting
 * always updates this state, the server just decides whether anyone's listening.
 */
@Singleton
class PresentationBroadcastRepo @Inject constructor() {

    private val _slideState = MutableStateFlow<BroadcastState>(BroadcastState.Idle)
    val slideState: StateFlow<BroadcastState> = _slideState.asStateFlow()

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
        _slideState.value = BroadcastState.Slide(
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
        if (current is BroadcastState.Slide && current.verses.isNotEmpty()) {
            val safeIndex = index.coerceIn(0, current.verses.size - 1)
            if (safeIndex != current.currentIndex) {
                _slideState.value = current.copy(currentIndex = safeIndex)
            }
        }
    }

    /** Called when the presenter screen is closed — falls back to the waiting page. */
    fun publishIdle() {
        _slideState.value = BroadcastState.Idle
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
