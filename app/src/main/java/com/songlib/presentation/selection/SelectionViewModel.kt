package com.songlib.presentation.selection

import android.util.Log
import androidx.lifecycle.*
import com.songlib.data.models.Book
import com.songlib.domain.entity.*
import com.songlib.domain.repos.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.*
import retrofit2.HttpException
import javax.inject.Inject

@HiltViewModel
class SelectionViewModel @Inject constructor(
    private val prefsRepo: PrefsRepo,
    private val subsRepo: SubsRepo,
    private val songbkRepo: SongBookRepo,
) : ViewModel() {
    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val _books = MutableStateFlow<List<Selectable<Book>>>(emptyList())
    val books: StateFlow<List<Selectable<Book>>> get() = _books

    private val _showUpgradeDialog = MutableStateFlow(false)
    val showUpgradeDialog: StateFlow<Boolean> = _showUpgradeDialog.asStateFlow()

    private val _pendingBookSelection = MutableStateFlow<Selectable<Book>?>(null)
    val pendingBookSelection: StateFlow<Selectable<Book>?> = _pendingBookSelection.asStateFlow()

    private val _isProUser = MutableStateFlow(false)
    val isProUser: StateFlow<Boolean> = _isProUser.asStateFlow()

    private fun getSelectedIds(): Set<Int> =
        prefsRepo.selectedBooks
            .split(",")
            .mapNotNull { it.toIntOrNull() }
            .toSet()

    fun fetchBooks() {
        _uiState.tryEmit(UiState.Loading)

        viewModelScope.launch {
            songbkRepo.fetchRemoteBooks().catch { exception ->
                Log.d("TAG", "fetching books error")
                val errorMessage = when (exception) {
                    is HttpException -> "Oops! We can't access the songbooks at the moment due to a HTTP Error: ${exception.code()} error on our server. Kindly try again a little later."
                    else -> "Oops! We can't access the songbooks at the moment due to a ${exception.message} error on our server. Kindly try again a little later."
                }
                Log.d("TAG", errorMessage)
                _uiState.tryEmit(UiState.Error(errorMessage))
            }.collect { respData ->
                val selectableBooks = respData.map { book->
                    Selectable(book, book.bookId in getSelectedIds())
                }
                _books.emit(selectableBooks)
                Log.d("TAG", "${_books.value.size} books fetched")
                _isProUser.value = prefsRepo.isProUser
                _uiState.tryEmit(UiState.Loaded)
            }
        }
    }

    fun getSelectedBookList(): List<Book> {
        return _books.value.filter { it.isSelected }.map { it.data }
    }

    fun saveSelectedBooks() {
        saveBooks(getSelectedBookList())
    }

    private suspend fun refreshSubscription(isOnline: Boolean) {
        if (!prefsRepo.isProUser) {
            subsRepo.isProUser(isOnline) { isActive ->
                prefsRepo.isProUser = isActive
                _isProUser.value = isActive
            }
        }
    }

    private fun saveBooks(books: List<Book>) {
        _uiState.tryEmit(UiState.Saving)
        Log.d("TAG", "saving ${books.size} books")

        viewModelScope.launch {
            try {
                if (prefsRepo.selectAfresh) {
                    val existingIds = getSelectedIds()
                    val newIds = books.map { it.bookId }.toSet()

                    val booksToInsert = books.filter { it.bookId !in existingIds }
                    val idsToDelete = existingIds - newIds

                    idsToDelete.forEach { songbkRepo.deleteById(it) }
                    booksToInsert.forEach { songbkRepo.saveBook(it) }

                    prefsRepo.selectedBooks = newIds.joinToString(",")
                } else {
                    prefsRepo.isDataSelected = true
                    songbkRepo.saveBooks(books)
                    prefsRepo.selectedBooks = books.joinToString(",") { it.bookId.toString() }
                }

                fetchRemoteSongs(books.map { it.bookId })
            } catch (e: Exception) {
                Log.e("SaveBooks", "Failed to save books", e)
                _uiState.emit(UiState.Error("Failed to save books: ${e.message}"))
            }
        }
    }

    private suspend fun fetchRemoteSongs(bookIds: List<Int>) {
        try {
            Log.d("TAG", "Starting song fetch for ${bookIds.size} books")
            withContext(Dispatchers.IO) {
                songbkRepo.fetchAndSaveSongs(bookIds)
            }

            prefsRepo.isDataLoaded = true
            Log.d("TAG", "Song fetch and save completed")
            _uiState.tryEmit(UiState.Saved)

        } catch (e: Exception) {
            Log.e("TAG", "Song fetch failed: ${e.message}", e)
            _uiState.tryEmit(UiState.Saved)
        }
    }

    fun toggleBookSelection(book: Selectable<Book>) {
        val currentSelectedCount = _books.value.count { it.isSelected }
        val isCurrentlySelected = book.isSelected

        if (isCurrentlySelected || _isProUser.value) {
            _books.value = _books.value.map {
                if (it.data.bookId == book.data.bookId) it.copy(isSelected = !it.isSelected) else it
            }
            return
        }

        if (currentSelectedCount >= 4) {
            _pendingBookSelection.value = book
            _showUpgradeDialog.value = true
        } else {
            _books.value = _books.value.map {
                if (it.data.bookId == book.data.bookId) it.copy(isSelected = !it.isSelected) else it
            }
        }
    }

    fun onUpgradeProceed() {
        _showUpgradeDialog.value = false
        _pendingBookSelection.value = null
    }

    fun onUpgradeDismis() {
        _showUpgradeDialog.value = false
        _pendingBookSelection.value = null
    }
}