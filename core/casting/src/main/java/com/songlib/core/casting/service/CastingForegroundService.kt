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
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import com.songlib.core.casting.CastingRepo
import com.songlib.core.casting.model.ServerStatus
import com.songlib.core.casting.server.CastingHttpServer
import com.songlib.core.casting.util.NetworkUtils
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * Keeps [CastingHttpServer] alive independently of whatever screen is on
 * top, and surfaces a persistent notification (with a Stop action) while
 * broadcasting — this is what lets the phone keep mirroring even if the
 * presenter dims the screen or briefly switches apps.
 */
@AndroidEntryPoint
class CastingForegroundService : Service() {

    @Inject
    lateinit var repo: CastingRepo

    private var httpServer: CastingHttpServer? = null

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_STOP -> {
                stopCasting()
                stopSelf()
                return START_NOT_STICKY
            }

            else -> startCasting()
        }
        return START_STICKY
    }

    private fun startCasting() {
        if (httpServer != null) return

        repo.setServerStatus(ServerStatus.Starting)

        try {
            ServiceCompat.startForeground(
                this,
                NOTIFICATION_ID,
                buildNotification(),
                ServiceInfo.FOREGROUND_SERVICE_TYPE_CONNECTED_DEVICE,
            )

            val newServer = CastingHttpServer(repo, port = CastingHttpServer.DEFAULT_PORT)
            newServer.start()
            httpServer = newServer

            val urls = NetworkUtils.getLocalIpAddresses()
                .map { ip -> "http://$ip:${CastingHttpServer.DEFAULT_PORT}" }
            repo.setServerStatus(ServerStatus.Running(urls, CastingHttpServer.DEFAULT_PORT))
        } catch (e: Exception) {
            repo.setServerStatus(ServerStatus.Error(e.message ?: "Couldn't start the casting server"))
            stopSelf()
        }
    }

    private fun stopCasting() {
        httpServer?.stop()
        httpServer = null
        repo.publishIdle()
        repo.setServerStatus(ServerStatus.Stopped)
    }

    override fun onDestroy() {
        stopCasting()
        super.onDestroy()
    }

    private fun buildNotification(): Notification {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = getSystemService(NotificationManager::class.java)
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Presentation casting",
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
            .setContentTitle("SongLib is casting to PC")
            .setContentText("Your presenter screen is being mirrored on your local network")
            .setSmallIcon(android.R.drawable.ic_menu_share)
            .setOngoing(true)
            .addAction(android.R.drawable.ic_menu_close_clear_cancel, "Stop", stopPendingIntent)
            .build()
    }

    companion object {
        const val ACTION_STOP = "com.songlib.casting.action.STOP"
        private const val CHANNEL_ID = "songlib_casting_channel"
        private const val NOTIFICATION_ID = 4242

        fun startIntent(context: Context): Intent =
            Intent(context, CastingForegroundService::class.java)

        fun stopIntent(context: Context): Intent =
            Intent(context, CastingForegroundService::class.java).setAction(ACTION_STOP)
    }
}
