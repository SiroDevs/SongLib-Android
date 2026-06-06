package com.songlib.core.common.utils

object ApiConstants {
    const val SONGLIB_BASE = "https://songlive.vercel.app/"
    const val SONGLIB_BOOKS = "api/books"
    const val SONGLIB_SONGS = "api/songs"

    const val PESAPAL_BASE_URL = "https://pay.pesapal.com/v3/api/"

    const val PESAPAL_AUTH  = "Auth/RequestToken"
    const val PESAPAL_ORDER = "Transactions/SubmitOrderRequest"

    const val CALLBACK_URL = "https://songlive.vercel.app/donation/callback"
    const val DONOR_EMAIL  = "donor@swahilib.app"
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
    const val LAST_SYNCED_AT = "last_synced_at"
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
