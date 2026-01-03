package com.songlib.presentation.splash

import android.content.Context
import androidx.lifecycle.*
import com.songlib.core.helpers.NetworkUtils
import com.songlib.domain.repos.PrefsRepo
import com.songlib.domain.repos.SubsRepo
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

    private val _selectAfresh = MutableStateFlow(prefsRepo.selectAfresh)
    val selectAfresh: StateFlow<Boolean> = _selectAfresh.asStateFlow()

    private val _isDataLoaded = MutableStateFlow(prefsRepo.isDataLoaded)
    val isDataLoaded: StateFlow<Boolean> = _isDataLoaded.asStateFlow()

    private val _isDataSelected = MutableStateFlow(prefsRepo.isDataSelected)
    val isDataSelected: StateFlow<Boolean> = _isDataSelected.asStateFlow()

    fun initializeApp(context: Context) {
        viewModelScope.launch {
            _selectAfresh.value = prefsRepo.selectAfresh
            _isDataLoaded.value = prefsRepo.isDataLoaded
            _isDataSelected.value = prefsRepo.isDataSelected

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