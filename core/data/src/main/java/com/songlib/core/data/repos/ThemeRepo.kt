package com.songlib.core.data.repos

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

enum class ThemeMode { SYSTEM, LIGHT, DARK }

@HiltViewModel
class ThemeRepo @Inject constructor(
    private val prefs: PrefsRepo
) : ViewModel() {
    var selectedTheme by mutableStateOf(prefs.appThemeMode)
        private set

    fun setTheme(mode: ThemeMode) {
        prefs.appThemeMode = mode
        selectedTheme = mode
    }
}

fun appThemeName(mode: ThemeMode):String {
    return when (mode){
        ThemeMode.SYSTEM -> "System Default"
        ThemeMode.LIGHT -> "Light Theme"
        ThemeMode.DARK -> "Dark Theme"
    }
}