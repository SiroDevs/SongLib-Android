package com.songlib.core.data.repos

import android.content.Context
import com.songlib.core.common.utils.PrefConstants
import dagger.hilt.android.qualifiers.ApplicationContext
import androidx.core.content.edit
import com.songlib.core.common.entity.DonationMethod
import com.songlib.core.common.utils.CrossPromo
import java.util.Calendar
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PrefsRepo @Inject constructor(
    @ApplicationContext context: Context
) {
    private val prefs =
        context.getSharedPreferences(PrefConstants.PREFERENCE_FILE, Context.MODE_PRIVATE)

    var installDate: Long
        get() = prefs.getLong(PrefConstants.INSTALL_DATE, 0L)
        set(value) = prefs.edit { putLong(PrefConstants.INSTALL_DATE, value) }

    var initialBooks: String
        get() = prefs.getString(PrefConstants.INITIAL_BOOKS, "") ?: ""
        set(value) = prefs.edit { putString(PrefConstants.INITIAL_BOOKS, value) }

    var selectedBooks: String
        get() = prefs.getString(PrefConstants.SELECTED_BOOKS, "1,2") ?: ""
        set(value) = prefs.edit { putString(PrefConstants.SELECTED_BOOKS, value) }

    var isDataSelected: Boolean
        get() = prefs.getBoolean(PrefConstants.IS_DATA_SELECTED, false)
        set(value) = prefs.edit { putBoolean(PrefConstants.IS_DATA_SELECTED, value) }

    var selectAfresh: Boolean
        get() = prefs.getBoolean(PrefConstants.SELECT_A_FRESH, false)
        set(value) = prefs.edit { putBoolean(PrefConstants.SELECT_A_FRESH, value) }

    var isDataLoaded: Boolean
        get() = prefs.getBoolean(PrefConstants.IS_DATA_LOADED, false)
        set(value) = prefs.edit { putBoolean(PrefConstants.IS_DATA_LOADED, value) }

    var appThemeMode: ThemeMode
        get() = ThemeMode.valueOf(
            prefs.getString(PrefConstants.THEME_MODE, ThemeMode.SYSTEM.name)
                ?: ThemeMode.SYSTEM.name
        )
        set(value) = prefs.edit { putString(PrefConstants.THEME_MODE, value.name) }

    var horizontalSlides: Boolean
        get() = prefs.getBoolean(PrefConstants.HORIZONTAL_SLIDES, false)
        set(value) = prefs.edit { putBoolean(PrefConstants.HORIZONTAL_SLIDES, value) }

    var demoMode: Boolean
        get() = prefs.getBoolean(PrefConstants.DEMO_MODE, false)
        set(value) = prefs.edit { putBoolean(PrefConstants.DEMO_MODE, value) }

    var autoPlayEnabled: Boolean
        get() = prefs.getBoolean(PrefConstants.AUTO_PLAY_ENABLED, true)
        set(value) = prefs.edit { putBoolean(PrefConstants.AUTO_PLAY_ENABLED, value) }

    var donationDoneAt: Long
        get() = prefs.getLong(PrefConstants.DONATION_DONE_AT, 0L)
        set(value) = prefs.edit { putLong(PrefConstants.DONATION_DONE_AT, value) }

    var donationMethod: DonationMethod
        get() = runCatching {
            DonationMethod.valueOf(
                prefs.getString(PrefConstants.DONATION_METHOD, DonationMethod.DIY.name)
                    ?: DonationMethod.DIY.name
            )
        }.getOrDefault(DonationMethod.DIY)
        set(value) = prefs.edit { putString(PrefConstants.DONATION_METHOD, value.name) }

    var donationRemindNextOpen: Boolean
        get() = prefs.getBoolean(PrefConstants.DONATION_REMIND_NEXT_OPEN, false)
        set(value) = prefs.edit { putBoolean(PrefConstants.DONATION_REMIND_NEXT_OPEN, value) }

    fun shouldShowDonation(): Boolean {
        val now = System.currentTimeMillis()
        val oneDayMs = 24 * 60 * 60 * 1000L
        if (installDate == 0L || now - installDate < oneDayMs) return false

        val donated = donationDoneAt
        if (donated == 0L) return true

        val monthsUntilNextPrompt = when (donationMethod) {
            DonationMethod.DIY -> 3
            DonationMethod.PAYSTACK -> 3
            DonationMethod.CRYPTO -> 5
        }
        val nextPromptAt = Calendar.getInstance().apply {
            timeInMillis = donated
            add(Calendar.MONTH, monthsUntilNextPrompt)
        }.timeInMillis

        return now >= nextPromptAt
    }

    fun recordDonation(method: DonationMethod = DonationMethod.DIY) {
        donationDoneAt = System.currentTimeMillis()
        donationMethod = method
        donationRemindNextOpen = false
    }

    var churchLibPromoDone: Boolean
        get() = prefs.getBoolean(PrefConstants.CHURCHLIB_PROMO_DONE, false)
        set(value) = prefs.edit { putBoolean(PrefConstants.CHURCHLIB_PROMO_DONE, value) }

    private var churchLibPromoRemindAt: Long
        get() = prefs.getLong(PrefConstants.CHURCHLIB_PROMO_REMIND_AT, 0L)
        set(value) = prefs.edit { putLong(PrefConstants.CHURCHLIB_PROMO_REMIND_AT, value) }

    fun churchLibPromoWaitMs(): Long? {
        if (churchLibPromoDone || installDate == 0L) return null
        val remindAt = churchLibPromoRemindAt
        val dueAt = if (remindAt > 0L) remindAt else installDate + CrossPromo.PROMO_FIRST_DELAY_MS
        return (dueAt - System.currentTimeMillis()).coerceAtLeast(0L)
    }

    fun remindChurchLibPromoLater() {
        churchLibPromoRemindAt = System.currentTimeMillis() + CrossPromo.PROMO_REMIND_DELAY_MS
    }

    fun markChurchLibPromoSeen() {
        churchLibPromoDone = true
    }

    var lastSyncedAt: Long
        get() = prefs.getLong(PrefConstants.LAST_SYNCED_AT, 0L)
        set(value) = prefs.edit { putLong(PrefConstants.LAST_SYNCED_AT, value) }

    fun needsDailySync(): Boolean {
        val last = lastSyncedAt
        if (last == 0L) return false
        val elapsed = System.currentTimeMillis() - last
        val oneDayMs = 24 * 60 * 60 * 1000L
        return elapsed >= oneDayMs
    }

    var loggedInUserId: Int
        get() = prefs.getInt(PrefConstants.LOGGED_IN_USER_ID, 0)
        set(value) = prefs.edit { putInt(PrefConstants.LOGGED_IN_USER_ID, value) }

    var loggedInEmail: String
        get() = prefs.getString(PrefConstants.LOGGED_IN_EMAIL, "") ?: ""
        set(value) = prefs.edit { putString(PrefConstants.LOGGED_IN_EMAIL, value) }

    var loggedInName: String
        get() = prefs.getString(PrefConstants.LOGGED_IN_NAME, "") ?: ""
        set(value) = prefs.edit { putString(PrefConstants.LOGGED_IN_NAME, value) }

    var loggedInPhotoUrl: String
        get() = prefs.getString(PrefConstants.LOGGED_IN_PHOTO_URL, "") ?: ""
        set(value) = prefs.edit { putString(PrefConstants.LOGGED_IN_PHOTO_URL, value) }

    var loggedInRole: String
        get() = prefs.getString(PrefConstants.LOGGED_IN_ROLE, "user") ?: "user"
        set(value) = prefs.edit { putString(PrefConstants.LOGGED_IN_ROLE, value) }

    val isLoggedIn: Boolean get() = loggedInUserId > 0

    val isAdmin: Boolean get() = loggedInRole == "admin"

    fun clearUser() {
        loggedInUserId = 0
        loggedInEmail = ""
        loggedInName = ""
        loggedInPhotoUrl = ""
        loggedInRole = "user"
    }

    var lastSinceDateIso: String
        get() = prefs.getString(PrefConstants.LAST_SINCE_DATE, "") ?: ""
        set(value) = prefs.edit { putString(PrefConstants.LAST_SINCE_DATE, value) }

    fun resetAppData() {
        isDataLoaded = false
        isDataSelected = false
        selectAfresh = false
        initialBooks = ""
        selectedBooks = ""
        clearUser()
    }
}
