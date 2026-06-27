package com.songlib.feature.broadcast

import android.content.Context
import androidx.lifecycle.ViewModel
import com.songlib.core.broadcast.PresentationBroadcastRepo
import com.songlib.core.broadcast.model.BroadcastState
import com.songlib.core.broadcast.model.ServerStatus
import com.songlib.core.broadcast.service.BroadcastForegroundService
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class BroadcastViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val repo: PresentationBroadcastRepo,
) : ViewModel() {

    val serverStatus: StateFlow<ServerStatus> = repo.serverStatus
    val slideState: StateFlow<BroadcastState> = repo.slideState
    val connectedClients: StateFlow<Int> = repo.connectedClients

    fun startBroadcasting() {
        context.startForegroundService(BroadcastForegroundService.startIntent(context))
    }

    fun stopBroadcasting() {
        context.startService(BroadcastForegroundService.stopIntent(context))
    }
}
