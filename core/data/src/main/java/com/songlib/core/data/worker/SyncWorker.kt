package com.songlib.core.data.worker

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.songlib.core.common.helpers.NetworkUtils
import com.songlib.core.data.repos.DraftRepo
import com.songlib.core.data.repos.EditorRepo
import com.songlib.core.data.repos.PrefsRepo
import com.songlib.core.data.repos.SongBookRepo
import com.songlib.core.data.repos.UserRepo
import com.songlib.core.database.model.BookEntity
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@HiltWorker
class SyncWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted workerParams: WorkerParameters,
    private val songbkRepo: SongBookRepo,
    private val prefsRepo: PrefsRepo,
    private val draftRepo: DraftRepo,
    private val editorRepo: EditorRepo,
    private val userRepo: UserRepo,
) : CoroutineWorker(context, workerParams) {

    private fun getSelectedIds(): Set<Int> =
        prefsRepo.selectedBooks
            .split(",")
            .mapNotNull { it.trim().toIntOrNull() }
            .toSet()

    override suspend fun doWork(): Result {
        if (!NetworkUtils.isNetworkAvailable(context)) {
            Log.w(TAG, "No network – retrying later")
            return Result.retry()
        }

        return try {
            Log.d(TAG, "▶ SyncWorker starting…")

            val selectedIds = getSelectedIds()
            val books = mutableListOf<BookEntity>()
            songbkRepo.fetchRemoteBooks(selectedIds).collect { fetched -> books.addAll(fetched) }

            if (books.isNotEmpty()) {
                songbkRepo.saveBooks(books)
                val bookIds = books.map { it.bookId }
                Log.d(TAG, "Fetched ${books.size} books, syncing songs for $bookIds")

                // Delta sync: use since= if we have a previous sync timestamp
                val since = prefsRepo.lastSinceDateIso.takeIf { it.isNotEmpty() }
                songbkRepo.fetchAndSaveSongs(bookIds, since = since)
            } else {
                Log.w(TAG, "⚠️ No books returned – skipping song fetch")
            }

            // Record new since timestamp
            val isoNow = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US).format(Date())
            prefsRepo.lastSinceDateIso = isoNow
            prefsRepo.isDataLoaded = true
            prefsRepo.lastSyncedAt = System.currentTimeMillis()

            // Post-login sync: push feature/edits to remote if logged in
            val userId = prefsRepo.loggedInUserId
            if (userId > 0) {
                draftRepo.syncDraftsToRemote(userId)
                editorRepo.syncEditsToRemote(userId)
                editorRepo.syncEditStatuses(userId)
                userRepo.syncBookSelection(userId)
                Log.d(TAG, "✅ User data synced for userId=$userId")
            }

            Log.d(TAG, "✅ SyncWorker completed successfully")
            Result.success()

        } catch (e: Exception) {
            Log.e(TAG, "❌ SyncWorker failed: ${e.message}", e)
            Result.retry()
        }
    }

    companion object {
        const val TAG = "SyncWorker"
        const val DAILY_SYNC_WORK_NAME   = "songlib_daily_sync"
        const val INSTALL_SYNC_WORK_NAME = "songlib_install_sync"
    }
}
