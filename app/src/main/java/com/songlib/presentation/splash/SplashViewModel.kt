package com.songlib.presentation.splash

import android.content.Context
import androidx.lifecycle.*
import com.songlib.core.helpers.NetworkUtils
import com.songlib.domain.repository.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val prefsRepo: PrefsRepo,
    private val subsRepo: SubsRepo,
) : ViewModel() {
    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun initializeApp(context: Context) {
        viewModelScope.launch {
            try {
                if (NetworkUtils.isNetworkAvailable(context)) {
                    checkSubscriptionAndTime(true)
                } else {
                    checkSubscriptionAndTime(false)
                }
                _isLoading.value = false
            } catch (e: Exception) {
                _isLoading.value = false
            } finally {
                _isLoading.value = false
            }
        }
    }

    private suspend fun checkSubscriptionAndTime(isOnline: Boolean) {
        if (!prefsRepo.isProUser) {
            subsRepo.isProUser(isOnline) { isActive ->
                prefsRepo.isProUser = isActive
            }
        }
        prefsRepo.updateAppOpenTime()
    }
}