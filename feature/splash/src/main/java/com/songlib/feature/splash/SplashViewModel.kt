package com.songlib.feature.splash

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.songlib.core.data.repos.PrefsRepo
import com.songlib.core.data.worker.SyncScheduler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val prefsRepo: PrefsRepo,
) : ViewModel() {

    private val _isReady = MutableStateFlow(false)
    val isReady: StateFlow<Boolean> = _isReady.asStateFlow()

    /**
     * Destination that the splash screen should navigate to once ready.
     * - SELECTION : user has never picked books (fresh install or selectAfresh flag set)
     * - HOME      : books already selected; worker handles any needed sync in background
     */
    private val _destination = MutableStateFlow<Destination>(Destination.Home)
    val destination: StateFlow<Destination> = _destination.asStateFlow()

    fun initializeApp(context: Context) {
        viewModelScope.launch {
            // Record install date on very first launch
            if (prefsRepo.installDate == 0L) {
                prefsRepo.installDate = System.currentTimeMillis()
            }

            when {
                // User has never completed book selection, or wants to re-select
                !prefsRepo.isDataSelected || prefsRepo.selectAfresh -> {
                    Log.d(TAG, "No selection yet (or re-select requested) → going to SELECTION")
                    _destination.value = Destination.Selection
                }

                // Books are selected but data has never been synced (e.g. sync failed before)
                !prefsRepo.isDataLoaded -> {
                    Log.d(TAG, "Data not loaded yet – scheduling install sync")
                    SyncScheduler.scheduleInstallSync(context)
                    _destination.value = Destination.Home
                }

                // Data is loaded; check if a daily re-sync is due
                prefsRepo.needsDailySync() -> {
                    Log.d(TAG, "Daily sync due – scheduling background sync")
                    SyncScheduler.scheduleDailySync(context)
                    _destination.value = Destination.Home
                }

                else -> {
                    Log.d(TAG, "Data is fresh – no sync needed today")
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

    companion object {
        private const val TAG = "SplashViewModel"
    }
}