package com.songlib.core.common.utils

object ApiConstants {
    const val BASE = "https://songlive.vercel.app/"
    const val BOOKS = "api/books"
    const val SONGS = "api/songs"
}

object PrefConstants {
    const val PREFERENCE_FILE = "app_pref"

    const val INITIAL_BOOKS = "initialBooks"
    const val SELECTED_BOOKS = "selectedBooks"

    const val IS_DATA_SELECTED = "dataSelected"
    const val IS_DATA_LOADED = "dataLoaded"
    const val SELECT_A_FRESH = "selectAfresh"
    const val INSTALL_DATE = "install_date"
    const val THEME_MODE = "themeMode"
    const val HORIZONTAL_SLIDES = "horizontalSlides"
    const val DEMO_MODE = "demoMode"

    const val DONATION_DONE_AT = "donation_done_at"
    const val DONATION_REMIND_NEXT_OPEN = "donation_remind_next"
}

object Routes {
    const val SPLASH = "splash"
    const val SELECTION = "selection"
    const val HOME = "home"
    const val PRESENTER = "presenter"
    const val LISTING = "listing"
    const val SETTINGS = "settings"
    const val HOW_IT_WORKS = "how_it_works"
    const val HELP = "help"
    const val DONATION = "donation"
}
