package com.songlib.feature.splash

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.songlib.core.common.helpers.NetworkUtils
import com.songlib.core.data.repos.PrefsRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val prefsRepo: PrefsRepo,
) : ViewModel() {
    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _isDataLoaded = MutableStateFlow(prefsRepo.isDataLoaded)
    val isDataLoaded: StateFlow<Boolean> = _isDataLoaded.asStateFlow()

    fun initializeApp(context: Context) {
        viewModelScope.launch {
            _isDataLoaded.value = prefsRepo.isDataLoaded

            try {
//                if (NetworkUtils.isNetworkAvailable(context)) {
//
//                } else {
//
//                }
                _isLoading.value = false
            } catch (e: Exception) {
                _isLoading.value = false
            } finally {
                _isLoading.value = false
            }
        }
    }
}