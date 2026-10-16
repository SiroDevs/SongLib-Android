package com.songlib.feature.selection.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.songlib.core.common.entity.Selectable
import com.songlib.core.common.entity.UiState
import com.songlib.core.common.utils.AppLimits
import com.songlib.core.data.repos.PrefsRepo
import com.songlib.core.data.repos.SongBookRepo
import com.songlib.core.data.repos.UserRepo
import com.songlib.core.data.worker.SyncScheduler
import com.songlib.core.database.model.BookEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import retrofit2.HttpException
import javax.inject.Inject

@HiltViewModel
class SelectionViewModel @Inject constructor(
    private val songbkRepo: SongBookRepo,
    private val prefsRepo: PrefsRepo,
    private val userRepo: UserRepo,
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val _books = MutableStateFlow<List<Selectable<BookEntity>>>(emptyList())
    val books: StateFlow<List<Selectable<BookEntity>>> get() = _books

    /** One-off messages for the UI (e.g. the songbook limit was reached). */
    private val _messages = MutableSharedFlow<String>(extraBufferCapacity = 1)
    val messages: SharedFlow<String> = _messages.asSharedFlow()

    private fun getSelectedIds(): Set<Int> =
        prefsRepo.selectedBooks
            .split(",")
            .mapNotNull { it.trim().toIntOrNull() }
            .toSet()

    fun fetchBooks() {
        _uiState.tryEmit(UiState.Loading)

        viewModelScope.launch {
            songbkRepo.fetchRemoteBooks().catch { exception ->
                val errorMessage = when (exception) {
                    is HttpException -> "Can't access songbooks right now (HTTP ${exception.code()}). Please try again shortly."
                    else -> "Can't access songbooks right now: ${exception.message}. Please try again shortly."
                }
                Log.e(TAG, "fetchBooks error: $errorMessage")
                _uiState.tryEmit(UiState.Error(errorMessage))
            }.collect { respData ->
                val selectableBooks = respData.map { book ->
                    Selectable(book, book.bookId in getSelectedIds())
                }
                _books.emit(selectableBooks)
                Log.d(TAG, "${_books.value.size} books fetched")
                _uiState.tryEmit(UiState.Loaded)
            }
        }
    }

    fun getSelectedBookList(): List<BookEntity> =
        _books.value.filter { it.isSelected }.map { it.data }

    /**
     * @param trimToLimit used by the headless auto-recover path, where nobody is
     * around to deselect books: keeps the first [AppLimits.MAX_SONGBOOKS].
     */
    fun saveSelectedBooks(context: Context, trimToLimit: Boolean = false) {
        val selected = getSelectedBookList()
        val toSave = if (trimToLimit) selected.take(AppLimits.MAX_SONGBOOKS) else selected
        if (toSave.size > AppLimits.MAX_SONGBOOKS) {
            val extra = toSave.size - AppLimits.MAX_SONGBOOKS
            _messages.tryEmit(
                "You can keep up to ${AppLimits.MAX_SONGBOOKS} songbooks. " +
                    "Please deselect $extra and try again."
            )
            return
        }
        saveBooks(toSave, context)
    }

    private fun saveBooks(books: List<BookEntity>, context: Context) {
        _uiState.tryEmit(UiState.Saving)
        Log.d(TAG, "Saving ${books.size} books")

        viewModelScope.launch {
            try {
                if (prefsRepo.selectAfresh) {
                    val existingIds = songbkRepo.fetchLocalBooks().map { it.bookId }.toSet()
                    val newIds = books.map { it.bookId }.toSet()
                    val booksToInsert = books.filter { it.bookId !in existingIds }
                    val idsToDelete = existingIds - newIds

                    idsToDelete.forEach {
                        songbkRepo.deleteById(it)
                        songbkRepo.deleteByBookId(it)
                    }
                    booksToInsert.forEach { songbkRepo.saveBook(it) }

                    prefsRepo.selectedBooks = newIds.joinToString(",")
                    prefsRepo.selectAfresh  = false
                } else {
                    songbkRepo.saveBooks(books)
                    prefsRepo.selectedBooks  = books.joinToString(",") { it.bookId.toString() }
                    prefsRepo.isDataSelected = true
                }

                prefsRepo.isDataLoaded = false

                val userId = prefsRepo.loggedInUserId
                if (userId > 0) {
                    userRepo.syncBookSelection(userId)
                }

                SyncScheduler.scheduleInstallSync(context)
                Log.d(TAG, "Books saved, WorkManager sync enqueued")
                _uiState.tryEmit(UiState.Saved)

            } catch (e: Exception) {
                Log.e(TAG, "Failed to save books", e)
                _uiState.emit(UiState.Error("Failed to save books: ${e.message}"))
            }
        }
    }

    fun toggleBookSelection(book: Selectable<BookEntity>) {
        val selectedCount = _books.value.count { it.isSelected }
        if (!book.isSelected && selectedCount >= AppLimits.MAX_SONGBOOKS) {
            _messages.tryEmit(
                "You can select up to ${AppLimits.MAX_SONGBOOKS} songbooks. " +
                    "Deselect one to add another."
            )
            return
        }
        _books.value = _books.value.map {
            if (it.data.bookId == book.data.bookId) it.copy(isSelected = !it.isSelected) else it
        }
    }

    companion object {
        private const val TAG = "SelectionViewModel"
    }
}
