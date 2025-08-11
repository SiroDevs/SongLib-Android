package com.songlib.domain.repository

import android.content.Context
import com.songlib.core.utils.PrefConstants

class PrefsRepository(context: Context) {
    private val prefs = context.getSharedPreferences(PrefConstants.PREFERENCE_FILE, Context.MODE_PRIVATE)

    var isDataSelected: Boolean
        get() = prefs.getBoolean(PrefConstants.DATA_SELECTED, false)
        set(value) = prefs.edit().putBoolean(PrefConstants.DATA_SELECTED, value).apply()

    var isDataLoaded: Boolean
        get() = prefs.getBoolean(PrefConstants.DATA_LOADED, false)
        set(value) = prefs.edit().putBoolean(PrefConstants.DATA_LOADED, value).apply()

    var themeMode: ThemeMode
        get() = ThemeMode.valueOf(prefs.getString("theme_mode", ThemeMode.SYSTEM.name)!!)
        set(value) = prefs.edit().putString("theme_mode", value.name).apply()
}
