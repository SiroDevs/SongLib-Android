package com.songlib

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.songlib.core.data.repos.PrefsRepo
import com.songlib.core.data.worker.SyncScheduler
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val prefsRepo: PrefsRepo,
    @ApplicationContext private val context: Context,
) : ViewModel() {
    private val _isReady = MutableStateFlow(false)
    val isReady: StateFlow<Boolean> = _isReady.asStateFlow()

    private val _destination = MutableStateFlow<Destination>(Destination.Home)
    val destination: StateFlow<Destination> = _destination.asStateFlow()

    init {
        initializeApp()
    }

    private fun initializeApp() {
        viewModelScope.launch {
            if (prefsRepo.installDate == 0L) {
                prefsRepo.installDate = System.currentTimeMillis()
            }

            when {
                !prefsRepo.isDataSelected || prefsRepo.selectAfresh -> {
                    _destination.value = Destination.Selection
                }
                !prefsRepo.isDataLoaded -> {
                    SyncScheduler.scheduleInstallSync(context)
                    _destination.value = Destination.Home
                }
                prefsRepo.needsDailySync() -> {
                    SyncScheduler.scheduleDailySync(context)
                    _destination.value = Destination.Home
                }
                else -> {
                    _destination.value = Destination.Home
                }
            }

            _isReady.value = true
        }
    }

    sealed interface Destination {
        data object Home : Destination
        data object Selection : Destination
    }
}