package com.songlib

import android.app.Application
import androidx.work.Configuration
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class SongLibApp : Application(), Configuration.Provider {

    /**
     * Injected by Hilt so WorkManager uses HiltWorkerFactory.
     * This is required for @HiltWorker / @AssistedInject to work.
     */
    @Inject
    lateinit var workerConfiguration: Configuration

    override val workManagerConfiguration: Configuration
        get() = workerConfiguration
}