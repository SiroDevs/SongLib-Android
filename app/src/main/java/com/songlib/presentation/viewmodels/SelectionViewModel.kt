package com.songlib.presentation.viewmodels

import android.util.Log
import androidx.lifecycle.*
import com.songlib.data.models.Book
import com.songlib.domain.entities.UiState
import com.songlib.domain.repositories.SelectionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.*
import retrofit2.HttpException
import javax.inject.Inject

@HiltViewModel
class SelectionViewModel @Inject constructor(
    private val selectionRepo: SelectionRepository,
) : ViewModel() {
    private var _isLoggedInStrava: MutableLiveData<Boolean> = MutableLiveData(null)
    var isLoggedInStrava: LiveData<Boolean> = _isLoggedInStrava

    private val _uiState: MutableStateFlow<UiState> = MutableStateFlow(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val _books = MutableStateFlow<List<Book>>(emptyList())
    val books: StateFlow<List<Book>> get() = _books

    init {
        //_isLoggedInStrava.postValue(sessionRepo.isLoggedIn())
    }

    fun fetchData() {
        _uiState.tryEmit(UiState.Loading)

        viewModelScope.launch {
            selectionRepo.getBooks().catch { exception ->
                Log.d("TAG", "fetchData: $exception")
                val errorCode = (exception as? HttpException)?.code()

                val errorMessage = if (errorCode in 400..499) {
                    "Error! Force Refresh"
                } else {
                    "We have some issues connecting to the server: $exception"
                }
                _uiState.tryEmit(UiState.Error(errorMessage))
            }.collect { respData ->
                _books.emit(respData)
            }
            _uiState.tryEmit(UiState.Loaded)
        }
    }

}
