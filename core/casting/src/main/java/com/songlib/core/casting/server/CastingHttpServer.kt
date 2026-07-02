package com.songlib.core.casting.server

import android.content.Context
import com.songlib.core.casting.data.CastingRepo
import com.songlib.core.casting.data.CastingState
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.install
import io.ktor.server.cio.CIO
import io.ktor.server.engine.EmbeddedServer
import io.ktor.server.engine.embeddedServer
import io.ktor.server.response.respond
import io.ktor.server.response.respondBytes
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import io.ktor.server.websocket.WebSockets
import io.ktor.server.websocket.pingPeriod
import io.ktor.server.websocket.timeout
import io.ktor.server.websocket.webSocket
import io.ktor.websocket.Frame
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.time.Duration.Companion.seconds

class CastingHttpServer(
    private val context: Context,
    private val repo: CastingRepo,
    private val port: Int = DEFAULT_PORT,
) {
    private val json = Json { encodeDefaults = true }
    private var server: EmbeddedServer<*, *>? = null

    private val logoBytes: ByteArray? by lazy { loadLogoBytes() }

    fun start() {
        if (server != null) return

        server = embeddedServer(CIO, port = port, host = "0.0.0.0") {
            install(WebSockets) {
                // Keep the WS alive over Android's LocalOnlyHotspot (which has
                // no internet and can silently drop idle connections) — without
                // pings the server never notices a dead peer and its `send`
                // hangs forever on the next state change.
                pingPeriod = 15.seconds
                timeout = 30.seconds
            }

            routing {
                get("/") {
                    call.respondText(WebClientPage.html, ContentType.Text.Html)
                }

                get("/logo.png") {
                    val bytes = logoBytes
                    if (bytes != null) {
                        call.respondBytes(bytes, ContentType.Image.PNG)
                    } else {
                        call.respond(HttpStatusCode.NotFound)
                    }
                }

                webSocket("/ws") {
                    repo.onClientConnected()
                    try {
                        send(Frame.Text(json.encodeToString(repo.slideState.value)))
                        repo.slideState
                            .collect { state: CastingState ->
                                send(Frame.Text(json.encodeToString(state)))
                            }
                    } catch (_: Exception) {
                        // The browser navigated away / refreshed — nothing to do.
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

    private fun loadLogoBytes(): ByteArray? = runCatching {
        val resId = context.resources.getIdentifier("app_icon", "drawable", context.packageName)
        if (resId == 0) return@runCatching null
        context.resources.openRawResource(resId).use { it.readBytes() }
    }.getOrNull()

    companion object {
        const val DEFAULT_PORT = 8080
    }
}
