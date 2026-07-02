package com.songlib.core.broadcast.server

import com.songlib.core.broadcast.PresentationBroadcastRepo
import com.songlib.core.broadcast.model.BroadcastState
import io.ktor.http.ContentType
import io.ktor.server.application.install
import io.ktor.server.cio.CIO
import io.ktor.server.engine.EmbeddedServer
import io.ktor.server.engine.embeddedServer
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import io.ktor.server.websocket.WebSockets
import io.ktor.server.websocket.webSocket
import io.ktor.websocket.Frame
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * Thin wrapper around a Ktor CIO embedded server. One route serves the static
 * mirror page, the other is a WebSocket that immediately sends the current
 * [PresentationBroadcastRepo.slideState] (so a browser that joins mid-song
 * catches up instantly) and then forwards every subsequent change.
 */
class BroadcastHttpServer(
    private val repo: PresentationBroadcastRepo,
    private val port: Int = DEFAULT_PORT,
) {
    private val json = Json { encodeDefaults = true }
    private var server: EmbeddedServer<*, *>? = null

    fun start() {
        if (server != null) return

        server = embeddedServer(CIO, port = port, host = "0.0.0.0") {
            install(WebSockets)

            routing {
                get("/") {
                    call.respondText(WebClientPage.html, ContentType.Text.Html)
                }

                webSocket("/ws") {
                    repo.onClientConnected()
                    try {
                        send(Frame.Text(json.encodeToString(repo.slideState.value)))
                        repo.slideState
                            .collect { state: BroadcastState ->
                                send(Frame.Text(json.encodeToString(state)))
                            }
                    } catch (_: Exception) {
                    } finally {
                        repo.onClientDisconnected()
                    }
                }
            }
        }

        server?.start(wait = false)
    }

    fun stop() {
        server?.stop(gracePeriodMillis = 200, timeoutMillis = 1000)
        server = null
    }

    companion object {
        const val DEFAULT_PORT = 8080
    }
}
