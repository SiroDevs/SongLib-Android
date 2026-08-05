package com.songlib.core.data.repos

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import com.songlib.core.network.dtos.UserDto
import com.songlib.core.network.services.SongLibService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepo @Inject constructor(
    private val service: SongLibService,
    private val prefsRepo: PreferencesRepo,
) {
    suspend fun loginOrRegister(
        googleId: String,
        email: String,
        name: String,
        photoUrl: String,
    ): Int {
        return try {
            val dto = UserDto(
                username = email.substringBefore("@"),
                email = email,
                name = name,
                photoUrl = photoUrl,
                googleId = googleId,
                selectedBooks = prefsRepo.selectedBooks
            )
            val user = service.createUser(dto)
            prefsRepo.loggedInUserId = user.userId
            prefsRepo.loggedInEmail = email
            prefsRepo.loggedInName = name
            prefsRepo.loggedInPhotoUrl = photoUrl
            prefsRepo.loggedInRole = user.role

            val remoteBooks = user.selectedBooks
            if (!remoteBooks.isNullOrEmpty() && prefsRepo.selectedBooks.isEmpty()) {
                prefsRepo.selectedBooks = remoteBooks
            }
            user.userId
        } catch (e: retrofit2.HttpException) {
            if (e.code() == 409) {
                val existing = parseExistingUser(e)
                if (existing != null) {
                    prefsRepo.loggedInUserId = existing.userId
                    prefsRepo.loggedInEmail = email
                    prefsRepo.loggedInName = name
                    prefsRepo.loggedInPhotoUrl = photoUrl
                    prefsRepo.loggedInRole = existing.role

                    val remoteBooks = existing.selectedBooks
                    if (!remoteBooks.isNullOrEmpty() && prefsRepo.selectedBooks.isEmpty()) {
                        prefsRepo.selectedBooks = remoteBooks
                    }
                    Log.d(
                        "UserRepo",
                        "User already exists — recovered userId=${existing.userId} from 409 body"
                    )
                    existing.userId
                } else {
                    Log.w(
                        "UserRepo",
                        "409 on createUser but response body had no usable user record"
                    )
                    throw e
                }
            } else throw e
        }
    }

    private fun parseExistingUser(e: retrofit2.HttpException): UserDto? {
        return try {
            val body = e.response()?.errorBody()?.string() ?: return null
            val parsed = Gson().fromJson(body, UserDto::class.java)
            if (parsed != null && parsed.userId > 0) parsed else null
        } catch (parseError: Exception) {
            Log.w("UserRepo", "Failed to parse existing user from 409 body: ${parseError.message}")
            null
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

    fun signOut() {
        FirebaseAuth.getInstance().signOut()
        prefsRepo.clearUser()
    }
}
