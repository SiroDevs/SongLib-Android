package com.songlib.feature.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.songlib.core.data.repos.DraftRepo
import com.songlib.core.data.repos.EditRepo
import com.songlib.core.data.repos.PrefsRepo
import com.songlib.core.data.repos.UserRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserProfileViewModel @Inject constructor(
    private val userRepo: UserRepo,
    private val prefsRepo: PrefsRepo,
    private val draftRepo: DraftRepo,
    private val editRepo: EditRepo,
) : ViewModel() {

    sealed interface AuthState {
        object Idle    : AuthState
        object Loading : AuthState
        data class Success(val userId: Int) : AuthState
        data class Error(val message: String) : AuthState
    }

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    val isLoggedIn:   Boolean get() = prefsRepo.isLoggedIn
    val userName:     String  get() = prefsRepo.loggedInName
    val userEmail:    String  get() = prefsRepo.loggedInEmail
    val userPhotoUrl: String  get() = prefsRepo.loggedInPhotoUrl

    fun loginOrRegister(googleId: String, email: String, name: String, photoUrl: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val userId = userRepo.loginOrRegister(googleId, email, name, photoUrl)
                // Post-login sync
                draftRepo.syncDraftsToRemote(userId)
                editRepo.syncEditsToRemote(userId)
                editRepo.syncEditStatuses(userId)
                userRepo.syncBookSelection(userId)
                _authState.value = AuthState.Success(userId)
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Login failed")
            }
        }
    }

    fun signOut() {
        userRepo.signOut()
        _authState.value = AuthState.Idle
    }
}
