package com.songlib.core.common.utils

import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

object ApiConstants {
    const val SONGLIB_BASE = "https://songlive.vercel.app/"

    const val API_VERSION = "api/v2/"

    const val BOOKS = "${API_VERSION}books"
    const val SONGS = "${API_VERSION}songs"
    const val DRAFTS = "${API_VERSION}drafts"
    const val USER_EDITS = "${API_VERSION}edits"
    const val REPORTS = "${API_VERSION}reports"
    const val USERS = "${API_VERSION}users"
    const val ORGANISATIONS = "${API_VERSION}organisations"
    const val LISTINGS = "${API_VERSION}listings"
    const val LIKES_TOGGLE = "${API_VERSION}songs/likes/toggle"
    const val LIKES_USER = "${API_VERSION}songs/likes"

    const val PAYSTACK_BASE_URL = "https://api.paystack.co/"
    const val PAYSTACK_INITIALIZE = "transaction/initialize"
    const val PAYSTACK_CALLBACK_URL = "https://songlive.vercel.app/donation/callback"
    const val DONOR_EMAIL = "anonymous_donor@songlib.app"
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
    const val LOGGED_IN_USER_ID = "logged_in_user_id"
    const val LOGGED_IN_EMAIL = "logged_in_email"
    const val LOGGED_IN_NAME = "logged_in_name"
    const val LOGGED_IN_PHOTO_URL = "logged_in_photo_url"
    const val LOGGED_IN_ROLE = "logged_in_role"
    const val LAST_SINCE_DATE = "last_since_date"
}

object Routes {
    const val SELECTION = "selection"
    const val HOME = "home"
    const val PRESENT = "present"
    const val LISTING = "listing"
    const val SETTINGS = "settings"
    const val HOW_IT_WORKS = "how_it_works"
    const val HELP = "help"
    const val DONATION = "donation"
    const val DRAFTS = "drafts"
    const val DRAFT_PRESENT = "draft_present"
    const val DRAFT_EDITOR  = "draft_editor"
    const val HISTORY = "history"
    const val USER_PROFILE = "user_profile"
    const val USER_EDITS = "user_edits"
    const val ADMIN_EDITS = "admin_edits"
    const val EDITOR = "editor"

    const val PAYMENT_WEBVIEW = "payment_webview/{redirectUrl}"

    fun paymentWebView(redirectUrl: String): String {
        val encoded = URLEncoder.encode(redirectUrl, StandardCharsets.UTF_8.toString())
        return "payment_webview/$encoded"
    }

    fun decodeRedirectUrl(encoded: String): String =
        URLDecoder.decode(encoded, StandardCharsets.UTF_8.toString())
}

object AppFonts {
    const val DEFAULT_FONT_SP = 28f
    const val MIN_FONT_SP = 14f
    const val MAX_FONT_SP = 60f
}
