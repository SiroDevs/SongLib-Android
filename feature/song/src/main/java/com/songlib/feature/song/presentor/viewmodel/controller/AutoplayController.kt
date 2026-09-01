package com.songlib.feature.song.presentor.viewmodel.controller

import com.songlib.core.casting.data.CastingRepo
import com.songlib.core.common.utils.AutoPlayDefaults
import com.songlib.core.data.repos.AutoPlayRepo
import com.songlib.core.database.model.AutoPlayEntity
import com.songlib.feature.song.presentor.utils.AutoPlayProgress
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.math.abs

/**
 * Owns the auto-advance stanza-timing engine. No duration is ever assumed: a verse or
 * chorus type only gets an auto-advance schedule once we've actually watched the user
 * linger on a page of that type — until then we just count elapsed time with no target
 * (see [AutoPlayProgress.isMonitoring]). Reads the current song's verses/indicators from
 * [presenter] rather than holding its own copy, so the two stay in sync automatically.
 */
class AutoplayController(
    private val autoPlayRepo: AutoPlayRepo,
    private val castingRepo: CastingRepo,
    private val presenter: PresenterController,
    private val scope: CoroutineScope,
    private val toastEvent: MutableSharedFlow<String>,
) {
    // Always starts false: whether a song's timing is already known or not, Auto Play
    // must never start moving verses on its own until the user taps the play button.
    private val _isAutoPlaying = MutableStateFlow(false)
    val isAutoPlaying: StateFlow<Boolean> = _isAutoPlaying.asStateFlow()

    /** Emits a page index the presenter's pager should scroll itself to. */
    private val _autoAdvanceTo = MutableSharedFlow<Int>()
    val autoAdvanceTo: SharedFlow<Int> = _autoAdvanceTo.asSharedFlow()

    private val _autoPlayProgress = MutableStateFlow(AutoPlayProgress())
    val autoPlayProgress: StateFlow<AutoPlayProgress> = _autoPlayProgress.asStateFlow()

    private var autoPlayJob: Job? = null
    private var progressTickerJob: Job? = null

    // Null means "not learned yet" - never a fabricated default. Populated from the DB by
    // loadDurations(), then refined live as the user actually moves between verses.
    private var learnedVerseDurationMs: Long? = null
    private var learnedChorusDurationMs: Long? = null

    private var currentPageIndex: Int = -1
    private var pageEnteredAt: Long = 0L

    /** The page index we ourselves just auto-advanced to (so we don't "learn" from it). */
    private var pendingAutoAdvanceIndex: Int? = null

    /** Where/when we last auto-advanced FROM, so a quick swipe-back can correct it. */
    private var lastAutoAdvanceFromIndex: Int? = null
    private var lastAutoAdvanceFromAt: Long = 0L

    private fun isChorusPage(index: Int): Boolean =
        presenter.indicators.value.getOrNull(index) == "C"

    /** The learned duration for this page's type, or null if we haven't observed one yet. */
    private fun durationForPage(index: Int): Long? =
        if (isChorusPage(index)) learnedChorusDurationMs else learnedVerseDurationMs

    private fun persistDurations() {
        val songId = presenter.currentSong.value?.songId ?: return
        // Nothing learned for either type yet - nothing meaningful to save.
        if (learnedVerseDurationMs == null && learnedChorusDurationMs == null) return
        val entity = AutoPlayEntity(
            songId = songId,
            verseDuration = learnedVerseDurationMs ?: 0L,
            chorusDuration = learnedChorusDurationMs ?: 0L,
        )
        scope.launch { autoPlayRepo.saveDurations(entity) }
    }

    /** First observation of a page type becomes its duration outright; later ones blend in. */
    private fun learnDuration(index: Int, elapsedMs: Long) {
        val clamped = elapsedMs.coerceIn(AutoPlayDefaults.MIN_DURATION_MS, AutoPlayDefaults.MAX_DURATION_MS)
        if (isChorusPage(index)) {
            learnedChorusDurationMs = learnedChorusDurationMs?.let { blend(it, clamped) } ?: clamped
        } else {
            learnedVerseDurationMs = learnedVerseDurationMs?.let { blend(it, clamped) } ?: clamped
        }
        persistDurations()
    }

    private fun blend(existing: Long, observed: Long): Long {
        val weight = AutoPlayDefaults.LEARNING_WEIGHT
        return (existing * (1 - weight) + observed * weight).toLong()
            .coerceIn(AutoPlayDefaults.MIN_DURATION_MS, AutoPlayDefaults.MAX_DURATION_MS)
    }

    /** The auto-advance away from [index] happened too soon — nudge its duration up. */
    private fun correctDurationUpward(index: Int) {
        if (isChorusPage(index)) {
            val current = learnedChorusDurationMs ?: return
            learnedChorusDurationMs = (current * AutoPlayDefaults.CORRECTION_FACTOR)
                .toLong()
                .coerceIn(AutoPlayDefaults.MIN_DURATION_MS, AutoPlayDefaults.MAX_DURATION_MS)
        } else {
            val current = learnedVerseDurationMs ?: return
            learnedVerseDurationMs = (current * AutoPlayDefaults.CORRECTION_FACTOR)
                .toLong()
                .coerceIn(AutoPlayDefaults.MIN_DURATION_MS, AutoPlayDefaults.MAX_DURATION_MS)
        }
        persistDurations()
    }

    /** Does nothing if [fromIndex]'s page type has no learned duration yet — we just
     *  monitor in that case, we never guess. */
    private fun scheduleAutoAdvance(fromIndex: Int) {
        autoPlayJob?.cancel()
        val nextIndex = fromIndex + 1
        if (nextIndex !in presenter.verses.value.indices) return

        val waitMs = durationForPage(fromIndex) ?: return
        autoPlayJob = scope.launch {
            delay(waitMs)
            pendingAutoAdvanceIndex = nextIndex
            lastAutoAdvanceFromIndex = fromIndex
            lastAutoAdvanceFromAt = System.currentTimeMillis()
            _autoAdvanceTo.emit(nextIndex)
        }
    }

    /** Toggled from the presenter's play/pause FAB. */
    fun toggleAutoPlay() {
        if (_isAutoPlaying.value) {
            stopAutoPlay(announce = false)
            return
        }
        _isAutoPlaying.value = true
        scope.launch {
            toastEvent.emit("Auto Play is on: The next stanza will move on its own")
        }
        if (currentPageIndex >= 0) scheduleAutoAdvance(currentPageIndex)
        startProgressTicker()
    }

    private fun stopAutoPlay(announce: Boolean) {
        _isAutoPlaying.value = false
        autoPlayJob?.cancel()
        stopProgressTicker()
        if (announce) {
            scope.launch {
                toastEvent.emit("Auto Play turned off — you jumped to a different verse")
            }
        }
    }

    /** Ticks roughly every 200ms so the Auto Play card can show a live counter + progress line. */
    private fun startProgressTicker() {
        progressTickerJob?.cancel()
        progressTickerJob = scope.launch {
            while (true) {
                if (currentPageIndex >= 0) {
                    val elapsedMs = (System.currentTimeMillis() - pageEnteredAt).coerceAtLeast(0L)
                    val knownDurationMs = durationForPage(currentPageIndex)
                    _autoPlayProgress.value = if (knownDurationMs == null) {
                        AutoPlayProgress(
                            elapsedSeconds = (elapsedMs / 1000L).toInt(),
                            totalSeconds = 0,
                            isMonitoring = true,
                        )
                    } else {
                        AutoPlayProgress(
                            elapsedSeconds = (elapsedMs / 1000L).toInt(),
                            totalSeconds = (knownDurationMs / 1000L).toInt().coerceAtLeast(1),
                            isMonitoring = false,
                        )
                    }
                }
                delay(200L)
            }
        }
    }

    private fun stopProgressTicker() {
        progressTickerJob?.cancel()
        progressTickerJob = null
        _autoPlayProgress.value = AutoPlayProgress()
    }

    fun loadDurations(songId: Int) {
        scope.launch {
            val entity = autoPlayRepo.getDurations(songId)
            learnedVerseDurationMs = entity?.verseDuration?.takeIf { it > 0 }
            learnedChorusDurationMs = entity?.chorusDuration?.takeIf { it > 0 }
            if (_isAutoPlaying.value && currentPageIndex >= 0) {
                scheduleAutoAdvance(currentPageIndex)
            }
        }
    }

    fun resetForNewSong() {
        autoPlayJob?.cancel()
        learnedVerseDurationMs = null
        learnedChorusDurationMs = null
        currentPageIndex = -1
        pageEnteredAt = 0L
        pendingAutoAdvanceIndex = null
        lastAutoAdvanceFromIndex = null
        lastAutoAdvanceFromAt = 0L
        _autoPlayProgress.value = AutoPlayProgress()
        if (_isAutoPlaying.value) startProgressTicker() else stopProgressTicker()
    }

    fun onVerseIndexChanged(index: Int) {
        castingRepo.updateIndex(index)

        val now = System.currentTimeMillis()
        val previousIndex = currentPageIndex
        val wasAutoAdvance = pendingAutoAdvanceIndex == index
        pendingAutoAdvanceIndex = null

        val isRealTransition = previousIndex >= 0 && previousIndex != index

        // A jump of more than one page (either direction) means the user is navigating
        // manually, not just stepping forward or correcting one page back - give up
        // Auto Play entirely rather than trying to guess what they're doing.
        if (_isAutoPlaying.value && isRealTransition && abs(index - previousIndex) > 1) {
            stopAutoPlay(announce = true)
            currentPageIndex = index
            pageEnteredAt = now
            return
        }

        if (isRealTransition && pageEnteredAt > 0 && !wasAutoAdvance) {
            // A manual swipe: learn how long the user actually lingered on that page.
            learnDuration(previousIndex, now - pageEnteredAt)

            // Swiped back to the page we just auto-advanced away from — it left too soon.
            if (index < previousIndex &&
                lastAutoAdvanceFromIndex == index &&
                now - lastAutoAdvanceFromAt < AutoPlayDefaults.CORRECTION_WINDOW_MS
            ) {
                correctDurationUpward(index)
            }
        }

        currentPageIndex = index
        pageEnteredAt = now

        if (!_isAutoPlaying.value) return

        val isLastPage = index >= presenter.verses.value.size - 1
        if (isLastPage) {
            // Nothing left to advance to - end the run instead of leaving it inert.
            stopAutoPlay(announce = false)
            return
        }

        scheduleAutoAdvance(index)
    }

    fun cancel() {
        autoPlayJob?.cancel()
        progressTickerJob?.cancel()
    }
}
