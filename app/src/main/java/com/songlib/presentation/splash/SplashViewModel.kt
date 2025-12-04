package com.songlib.presentation.splash

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.songlib.core.helpers.NetworkUtils
import com.songlib.domain.repository.PreferencesRepository
import com.songlib.domain.repository.SubscriptionsRepository
import com.songlib.presentation.navigation.Routes
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val prefsRepo: PreferencesRepository,
    private val subsRepo: SubscriptionsRepository,
) : ViewModel() {
    private val _nextRoute = MutableStateFlow(Routes.SPLASH)
    val nextRoute: StateFlow<String> = _nextRoute.asStateFlow()

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
                determineNextRoute()
            } catch (e: Exception) {
                determineNextRoute()
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

    private fun determineNextRoute() {
        _nextRoute.value = when {
            prefsRepo.selectAfresh -> Routes.STEP_1
            prefsRepo.isDataSelected && prefsRepo.isDataLoaded -> Routes.HOME
            prefsRepo.isDataSelected && !prefsRepo.isDataLoaded -> Routes.STEP_2
            else -> Routes.HOME
        }
    }
}