package com.songlib.feature.account.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.songlib.core.common.entity.AuthState
import com.songlib.core.data.repos.DraftRepo
import com.songlib.core.data.repos.EditorRepo
import com.songlib.core.data.repos.PreferencesRepo
import com.songlib.core.data.repos.UserRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class UserProfileState(
    val isLoggedIn: Boolean = false,
    val isAdmin: Boolean = false,
    val name: String = "",
    val email: String = "",
    val photoUrl: String = "",
)

@HiltViewModel
class AccountViewModel @Inject constructor(
    private val userRepo: UserRepo,
    private val prefsRepo: PreferencesRepo,
    private val draftRepo: DraftRepo,
    private val editorRepo: EditorRepo,
) : ViewModel() {
    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    private val _profile = MutableStateFlow(readProfileFromPrefs())
    val profile: StateFlow<UserProfileState> = _profile.asStateFlow()

    private fun readProfileFromPrefs() = UserProfileState(
        isLoggedIn = prefsRepo.isLoggedIn,
        isAdmin = prefsRepo.isAdmin,
        name = prefsRepo.loggedInName,
        email = prefsRepo.loggedInEmail,
        photoUrl = prefsRepo.loggedInPhotoUrl,
    )

    private fun refreshProfile() {
        _profile.value = readProfileFromPrefs()
    }

    fun loginOrRegister(googleId: String, email: String, name: String, photoUrl: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val userId = userRepo.loginOrRegister(googleId, email, name, photoUrl)
                draftRepo.syncDraftsToRemote(userId)
                editorRepo.syncEditsToRemote(userId)
                editorRepo.syncEditStatuses(userId)
                userRepo.syncBookSelection(userId)
                refreshProfile()
                _authState.value = AuthState.Success(userId)
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Login failed")
            }
        }
    }

    fun signInFailed(message: String) {
        _authState.value = AuthState.Error(message)
    }

    fun signOut() {
        userRepo.signOut()
        refreshProfile()
        _authState.value = AuthState.Idle
    }
}
