package com.songlib.core.data.repos

import android.util.Log
import com.songlib.core.network.dtos.UserDto
import com.songlib.core.network.services.SongLibService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepo @Inject constructor(
    private val service: SongLibService,
    private val prefsRepo: PrefsRepo,
) {
    suspend fun loginOrRegister(
        googleId: String,
        email: String,
        name: String,
        photoUrl: String,
    ): Int {
        return try {
            val dto = UserDto(
                username      = email.substringBefore("@"),
                email         = email,
                name          = name,
                photoUrl      = photoUrl,
                googleId      = googleId,
                selectedBooks = prefsRepo.selectedBooks
            )
            val user = service.createUser(dto)
            prefsRepo.loggedInUserId   = user.userId
            prefsRepo.loggedInEmail    = email
            prefsRepo.loggedInName     = name
            prefsRepo.loggedInPhotoUrl = photoUrl

            // If remote has book selection and local is empty, apply remote selection
            val remoteBooks = user.selectedBooks
            if (!remoteBooks.isNullOrEmpty() && prefsRepo.selectedBooks.isEmpty()) {
                prefsRepo.selectedBooks = remoteBooks
            }
            user.userId
        } catch (e: retrofit2.HttpException) {
            if (e.code() == 409) {
                // User already exists — store profile locally from Google data
                prefsRepo.loggedInEmail    = email
                prefsRepo.loggedInName     = name
                prefsRepo.loggedInPhotoUrl = photoUrl
                Log.d("UserRepo", "User already exists — stored local profile")
                prefsRepo.loggedInUserId
            } else throw e
        }
    }

    suspend fun syncBookSelection(userId: Int) {
        if (userId <= 0) return
        try {
            val current = service.getUser(userId)
            service.updateUser(userId, current.copy(selectedBooks = prefsRepo.selectedBooks))
        } catch (e: Exception) {
            Log.w("UserRepo", "syncBookSelection failed: ${e.message}")
        }
    }

    fun signOut() = prefsRepo.clearUser()
}
