package com.songlib

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
//import io.sentry.android.core.SentryAndroid

@HiltAndroidApp
class SongLibApp : Application() {

//    override fun onCreate() {
//        super.onCreate()
//
//        SentryAndroid.init(this) { options ->
//            options.dsn = BuildConfig.SentryDsn
//            options.tracesSampleRate = 1.0
//            options.isDebug = false
//        }
//    }
}
