package com.songlib.core.data.worker

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.songlib.core.common.helpers.NetworkUtils
import com.songlib.core.data.repos.PrefsRepo
import com.songlib.core.data.repos.SongBookRepo
import com.songlib.core.database.model.BookEntity
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

/**
 * Background worker that fetches books and songs from the remote API and
 * persists them to the local Room database.
 *
 * Enqueued in two scenarios:
 *  1. After selection – user picks their books, songs are fetched immediately.
 *  2. Once per day   – re-syncs any changes without blocking the UI.
 *
 * Uses @HiltWorker so repo dependencies are injected by Hilt.
 * Requires [HiltWorkerFactory] wired via [Configuration.Provider] in [SongLibApp].
 */
@HiltWorker
class SyncWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted workerParams: WorkerParameters,
    private val songbkRepo: SongBookRepo,
    private val prefsRepo: PrefsRepo,
) : CoroutineWorker(context, workerParams) {

    /** Parse the comma-separated selectedBooks pref into a Set<Int>. */
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

            coroutineScope {
                val selectedIds = getSelectedIds()

                val books = mutableListOf<BookEntity>()
                songbkRepo.fetchRemoteBooks(selectedIds).collect { fetched ->
                    books.addAll(fetched)
                }

                if (books.isNotEmpty()) {
                    songbkRepo.saveBooks(books)
                    val bookIds = books.map { it.bookId }
                    Log.d(TAG, "Fetched ${books.size} books, fetching songs for $bookIds")

                    songbkRepo.fetchAndSaveSongs(bookIds)
                } else {
                    Log.w(TAG, "⚠️ No books returned for selectedIds=$selectedIds – skipping song fetch")
                }
            }

            prefsRepo.isDataLoaded = true
            prefsRepo.lastSyncedAt = System.currentTimeMillis()
            Log.d(TAG, "✅ SyncWorker completed successfully")
            Result.success()

        } catch (e: Exception) {
            Log.e(TAG, "❌ SyncWorker failed: ${e.message}", e)
            Result.retry()
        }
    }

    companion object {
        const val TAG = "SyncWorker"
        /** Unique name for the daily re-sync request (deduplicates if app opened twice). */
        const val DAILY_SYNC_WORK_NAME = "songlib_daily_sync"
        /** Unique name for the post-selection sync on first install. */
        const val INSTALL_SYNC_WORK_NAME = "songlib_install_sync"
    }
}