package com.songlib.core.casting.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import com.songlib.core.casting.data.CastingRepo
import com.songlib.core.casting.data.HotspotStatus
import com.songlib.core.casting.data.ServerStatus
import com.songlib.core.casting.hotspot.HotspotController
import com.songlib.core.casting.hotspot.HotspotOutcome
import com.songlib.core.casting.server.CastingHttpServer
import com.songlib.core.common.utils.NetworkUtils
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class CastingForegroundService : Service() {
    @Inject
    lateinit var repo: CastingRepo

    private var httpServer: CastingHttpServer? = null
    private var hotspotController: HotspotController? = null
    private val mainHandler = Handler(Looper.getMainLooper())
    private val urlRefreshRunnables = mutableListOf<Runnable>()

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_STOP -> {
                stopEverything()
                stopSelf()
                return START_NOT_STICKY
            }

            ACTION_STOP_HOTSPOT -> {
                stopHotspotOnly()
            }

            ACTION_START_HOTSPOT -> {
                startCastingAndHotspot()
            }

            else -> {
                startCastingOnly()
            }
        }
        return START_STICKY
    }

    private fun startCastingOnly() {
        if (httpServer != null) return
        runCatching {
            promoteToForeground()
            launchServer()
        }.onFailure { e ->
            repo.setServerStatus(ServerStatus.Error(e.message ?: "Couldn't start casting"))
            stopSelf()
        }
    }

    private fun startCastingAndHotspot() {
        runCatching {
            promoteToForeground()
            if (httpServer == null) launchServer()
            launchHotspot()
        }.onFailure { e ->
            repo.setServerStatus(ServerStatus.Error(e.message ?: "Couldn't start casting"))
            repo.setHotspotStatus(HotspotStatus.Error(e.message ?: "Couldn't start the hotspot"))
            stopSelf()
        }
    }

    private fun promoteToForeground() {
        repo.setServerStatus(ServerStatus.Starting)
        ServiceCompat.startForeground(
            this,
            NOTIFICATION_ID,
            buildNotification(),
            ServiceInfo.FOREGROUND_SERVICE_TYPE_CONNECTED_DEVICE,
        )
    }

    private fun launchServer() {
        val newServer = CastingHttpServer(applicationContext, repo, port = CastingHttpServer.DEFAULT_PORT)
        newServer.start()
        httpServer = newServer
        repo.setServerStatus(ServerStatus.Running(currentUrl(), CastingHttpServer.DEFAULT_PORT))
    }

    private fun launchHotspot() {
        if (hotspotController?.isActive == true) {
            scheduleUrlRefreshWithRetries()
            return
        }

        if (NetworkUtils.hasHotspotIpAddress()) {
            repo.setHotspotStatus(HotspotStatus.Stopped)
            scheduleUrlRefreshWithRetries()
            return
        }

        repo.setHotspotStatus(HotspotStatus.Starting)
        val controller = hotspotController ?: HotspotController(applicationContext).also { hotspotController = it }
        controller.start { outcome ->
            when (outcome) {
                is HotspotOutcome.Success -> {
                    repo.setHotspotStatus(
                        HotspotStatus.Running(
                            ssid = outcome.info.ssid,
                            password = outcome.info.password,
                            isOpen = outcome.info.isOpen,
                        )
                    )
                    scheduleUrlRefreshWithRetries()
                }

                is HotspotOutcome.Failure -> {
                    repo.setHotspotStatus(HotspotStatus.Error(outcome.message))
                    // If the server is still Starting or has no reachable URL,
                    // the user has no way to actually cast — bubble the failure
                    // up so StatusCard doesn't sit on "Starting" forever.
                    val server = repo.serverStatus.value
                    val stranded = server is ServerStatus.Starting ||
                            (server is ServerStatus.Running && server.url == null)
                    if (stranded) {
                        repo.setServerStatus(ServerStatus.Error(outcome.message))
                    }
                    refreshCastingUrl()
                }
            }
        }
    }

    private fun stopHotspotOnly() {
        hotspotController?.stop()
        repo.setHotspotStatus(HotspotStatus.Stopped)
        cancelPendingUrlRefreshes()
        refreshCastingUrl()
    }

    private fun refreshCastingUrl() {
        if (repo.serverStatus.value is ServerStatus.Running) {
            repo.setServerStatus(ServerStatus.Running(currentUrl(), CastingHttpServer.DEFAULT_PORT))
        }
    }

    private fun scheduleUrlRefreshWithRetries() {
        cancelPendingUrlRefreshes()
        val delays = longArrayOf(300L, 700L, 1200L, 2000L)
        for (delay in delays) {
            val runnable = Runnable { refreshCastingUrl() }
            urlRefreshRunnables += runnable
            mainHandler.postDelayed(runnable, delay)
        }
    }

    private fun cancelPendingUrlRefreshes() {
        urlRefreshRunnables.forEach { mainHandler.removeCallbacks(it) }
        urlRefreshRunnables.clear()
    }

    private fun currentUrl(): String? =
        NetworkUtils.getPrimaryLocalIpAddress()?.let { ip -> "http://$ip:${CastingHttpServer.DEFAULT_PORT}" }

    private fun stopEverything() {
        cancelPendingUrlRefreshes()
        httpServer?.stop()
        httpServer = null
        hotspotController?.stop()
        hotspotController = null
        repo.publishIdle()
        repo.setServerStatus(ServerStatus.Stopped)
        repo.setHotspotStatus(HotspotStatus.Stopped)
    }

    override fun onDestroy() {
        stopEverything()
        super.onDestroy()
    }

    private fun buildNotification(): Notification {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = getSystemService(NotificationManager::class.java)
            val channel = NotificationChannel(
                CHANNEL_ID,
                "SongLib Casting",
                NotificationManager.IMPORTANCE_LOW,
            )
            manager.createNotificationChannel(channel)
        }

        val stopIntent = Intent(this, CastingForegroundService::class.java).setAction(ACTION_STOP)
        val stopPendingIntent = PendingIntent.getService(
            this,
            0,
            stopIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("SongLib is Casting")
            .setContentText("Your song presentation is being mirrored on your local network")
            .setSmallIcon(android.R.drawable.ic_menu_share)
            .setOngoing(true)
            .addAction(android.R.drawable.ic_menu_close_clear_cancel, "STOP CASTING", stopPendingIntent)
            .build()
    }

    companion object {
        const val ACTION_STOP = "com.songlib.casting.action.STOP"
        const val ACTION_START_HOTSPOT = "com.songlib.casting.action.START_HOTSPOT"
        const val ACTION_STOP_HOTSPOT = "com.songlib.casting.action.STOP_HOTSPOT"
        private const val CHANNEL_ID = "songlib_casting_channel"
        private const val NOTIFICATION_ID = 4242

        fun startIntent(context: Context): Intent =
            Intent(context, CastingForegroundService::class.java)

        fun stopIntent(context: Context): Intent =
            Intent(context, CastingForegroundService::class.java).setAction(ACTION_STOP)

        fun startHotspotIntent(context: Context): Intent =
            Intent(context, CastingForegroundService::class.java).setAction(ACTION_START_HOTSPOT)

        fun stopHotspotIntent(context: Context): Intent =
            Intent(context, CastingForegroundService::class.java).setAction(ACTION_STOP_HOTSPOT)
    }
}
