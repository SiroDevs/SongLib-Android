package com.songlib.core.broadcast.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import com.songlib.core.broadcast.PresentationBroadcastRepo
import com.songlib.core.broadcast.model.ServerStatus
import com.songlib.core.broadcast.server.BroadcastHttpServer
import com.songlib.core.broadcast.util.NetworkUtils
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * Keeps [BroadcastHttpServer] alive independently of whatever screen is on
 * top, and surfaces a persistent notification (with a Stop action) while
 * broadcasting — this is what lets the phone keep mirroring even if the
 * presenter dims the screen or briefly switches apps.
 */
@AndroidEntryPoint
class BroadcastForegroundService : Service() {

    @Inject
    lateinit var repo: PresentationBroadcastRepo

    private var httpServer: BroadcastHttpServer? = null

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_STOP -> {
                stopBroadcast()
                stopSelf()
                return START_NOT_STICKY
            }

            else -> startBroadcast()
        }
        return START_STICKY
    }

    private fun startBroadcast() {
        if (httpServer != null) return

        repo.setServerStatus(ServerStatus.Starting)
        ServiceCompat.startForeground(
            this,
            NOTIFICATION_ID,
            buildNotification(),
            ServiceInfo.FOREGROUND_SERVICE_TYPE_CONNECTED_DEVICE,
        )

        try {
            val newServer = BroadcastHttpServer(repo, port = BroadcastHttpServer.DEFAULT_PORT)
            newServer.start()
            httpServer = newServer

            val urls = NetworkUtils.getLocalIpAddresses()
                .map { ip -> "http://$ip:${BroadcastHttpServer.DEFAULT_PORT}" }
            repo.setServerStatus(ServerStatus.Running(urls, BroadcastHttpServer.DEFAULT_PORT))
        } catch (e: Exception) {
            repo.setServerStatus(ServerStatus.Error(e.message ?: "Couldn't start the broadcast server"))
            stopSelf()
        }
    }

    private fun stopBroadcast() {
        httpServer?.stop()
        httpServer = null
        repo.publishIdle()
        repo.setServerStatus(ServerStatus.Stopped)
    }

    override fun onDestroy() {
        stopBroadcast()
        super.onDestroy()
    }

    private fun buildNotification(): Notification {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = getSystemService(NotificationManager::class.java)
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Presentation broadcast",
                NotificationManager.IMPORTANCE_LOW,
            )
            manager.createNotificationChannel(channel)
        }

        val stopIntent = Intent(this, BroadcastForegroundService::class.java).setAction(ACTION_STOP)
        val stopPendingIntent = PendingIntent.getService(
            this,
            0,
            stopIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Broadcasting to PC")
            .setContentText("Your presenter screen is being mirrored on your local network")
            .setSmallIcon(android.R.drawable.ic_menu_share)
            .setOngoing(true)
            .addAction(android.R.drawable.ic_menu_close_clear_cancel, "Stop", stopPendingIntent)
            .build()
    }

    companion object {
        const val ACTION_STOP = "com.songlib.broadcast.action.STOP"
        private const val CHANNEL_ID = "broadcast_channel"
        private const val NOTIFICATION_ID = 4242

        fun startIntent(context: Context): Intent =
            Intent(context, BroadcastForegroundService::class.java)

        fun stopIntent(context: Context): Intent =
            Intent(context, BroadcastForegroundService::class.java).setAction(ACTION_STOP)
    }
}
