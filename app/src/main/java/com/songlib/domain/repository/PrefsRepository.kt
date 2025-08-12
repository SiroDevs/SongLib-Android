package com.songlib.domain.repository

import android.content.Context
import com.songlib.core.utils.PrefConstants
import androidx.core.content.edit
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PrefsRepository @Inject constructor(
    @ApplicationContext context: Context
) {
    private val prefs =
        context.getSharedPreferences(PrefConstants.PREFERENCE_FILE, Context.MODE_PRIVATE)

    var selectedBooks: String
        get() = prefs.getString(PrefConstants.SELECTED_BOOKS, "") ?: ""
        set(value) = prefs.edit { putString(PrefConstants.SELECTED_BOOKS, value) }

    var isDataSelected: Boolean
        get() = prefs.getBoolean(PrefConstants.DATA_SELECTED, false)
        set(value) = prefs.edit { putBoolean(PrefConstants.DATA_SELECTED, value) }

    var isDataLoaded: Boolean
        get() = prefs.getBoolean(PrefConstants.DATA_LOADED, false)
        set(value) = prefs.edit { putBoolean(PrefConstants.DATA_LOADED, value) }

    var appThemeMode: ThemeMode
        get() = ThemeMode.valueOf(
            prefs.getString(PrefConstants.THEME_MODE, ThemeMode.SYSTEM.name)
                ?: ThemeMode.SYSTEM.name
        )
        set(value) = prefs.edit { putString(PrefConstants.THEME_MODE, value.name) }

    var horizontalSlides: Boolean
        get() = prefs.getBoolean(PrefConstants.HORIZONTAL_SLIDES, false)
        set(value) = prefs.edit { putBoolean(PrefConstants.HORIZONTAL_SLIDES, value) }

}
