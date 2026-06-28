package com.songlib.feature.casting.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import com.songlib.core.data.repos.CastingRepo
import com.songlib.core.common.entity.CastingState
import com.songlib.core.common.entity.ServerStatus
import com.songlib.core.casting.service.CastingForegroundService
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class CastingViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    repo: CastingRepo,
) : ViewModel() {

    val serverStatus: StateFlow<ServerStatus> = repo.serverStatus
    val slideState: StateFlow<CastingState> = repo.slideState
    val connectedClients: StateFlow<Int> = repo.connectedClients

    fun startCasting() {
        context.startForegroundService(CastingForegroundService.Companion.startIntent(context))
    }

    fun stopCasting() {
        context.startService(CastingForegroundService.Companion.stopIntent(context))
    }
}