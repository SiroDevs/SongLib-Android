package com.songlib.feature.song.presentor.viewmodel.controller

import com.songlib.core.casting.data.CastingRepo
import com.songlib.core.common.utils.AutoPlayDefaults
import com.songlib.core.data.repos.AutoPlayRepo
import com.songlib.core.database.model.AutoPlayEntity
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

class AutoplayController(
    private val autoPlayRepo: AutoPlayRepo,
    private val castingRepo: CastingRepo,
    private val content: SongController,
    private val scope: CoroutineScope,
    private val toastEvent: MutableSharedFlow<String>,
    initiallyEnabled: Boolean,
) {
    private val _isAutoPlaying = MutableStateFlow(initiallyEnabled)
    val isAutoPlaying: StateFlow<Boolean> = _isAutoPlaying.asStateFlow()

    /** Emits a page index the presenter's pager should scroll itself to. */
    private val _autoAdvanceTo = MutableSharedFlow<Int>()
    val autoAdvanceTo: SharedFlow<Int> = _autoAdvanceTo.asSharedFlow()

    private var autoPlayJob: Job? = null
    private var songDurations: AutoPlayEntity? = null

    private var currentPageIndex: Int = -1
    private var pageEnteredAt: Long = 0L

    /** The page index we ourselves just auto-advanced to (so we don't "learn" from it). */
    private var pendingAutoAdvanceIndex: Int? = null

    /** Where/when we last auto-advanced FROM, so a quick swipe-back can correct it. */
    private var lastAutoAdvanceFromIndex: Int? = null
    private var lastAutoAdvanceFromAt: Long = 0L

    private fun isChorusPage(index: Int): Boolean =
        content.indicators.value.getOrNull(index) == "C"

    private fun durationsOrDefault(): AutoPlayEntity =
        songDurations ?: AutoPlayEntity(
            songId = content.currentSong.value?.songId ?: 0,
            verseDuration = AutoPlayDefaults.DEFAULT_VERSE_MS,
            chorusDuration = AutoPlayDefaults.DEFAULT_CHORUS_MS,
        )

    private fun durationForPage(index: Int): Long {
        val durations = durationsOrDefault()
        return if (isChorusPage(index)) durations.chorusDuration else durations.verseDuration
    }

    private fun persistDurations() {
        val entity = songDurations ?: return
        scope.launch { autoPlayRepo.saveDurations(entity) }
    }

    /** Blend a freshly observed dwell time into the learned duration for this page's type. */
    private fun learnDuration(index: Int, elapsedMs: Long) {
        val clamped = elapsedMs.coerceIn(AutoPlayDefaults.MIN_DURATION_MS, AutoPlayDefaults.MAX_DURATION_MS)
        val current = durationsOrDefault()
        val updated = if (isChorusPage(index)) {
            val blended = blend(current.chorusDuration, clamped)
            current.copy(chorusDuration = blended)
        } else {
            val blended = blend(current.verseDuration, clamped)
            current.copy(verseDuration = blended)
        }
        songDurations = updated
        persistDurations()
    }

    private fun blend(existing: Long, observed: Long): Long {
        val weight = AutoPlayDefaults.LEARNING_WEIGHT
        return (existing * (1 - weight) + observed * weight).toLong()
            .coerceIn(AutoPlayDefaults.MIN_DURATION_MS, AutoPlayDefaults.MAX_DURATION_MS)
    }

    /** The auto-advance away from [index] happened too soon — nudge its duration up. */
    private fun correctDurationUpward(index: Int) {
        val current = durationsOrDefault()
        val updated = if (isChorusPage(index)) {
            current.copy(
                chorusDuration = (current.chorusDuration * AutoPlayDefaults.CORRECTION_FACTOR)
                    .toLong()
                    .coerceIn(AutoPlayDefaults.MIN_DURATION_MS, AutoPlayDefaults.MAX_DURATION_MS)
            )
        } else {
            current.copy(
                verseDuration = (current.verseDuration * AutoPlayDefaults.CORRECTION_FACTOR)
                    .toLong()
                    .coerceIn(AutoPlayDefaults.MIN_DURATION_MS, AutoPlayDefaults.MAX_DURATION_MS)
            )
        }
        songDurations = updated
        persistDurations()
    }

    private fun scheduleAutoAdvance(fromIndex: Int) {
        autoPlayJob?.cancel()
        val nextIndex = fromIndex + 1
        if (nextIndex !in content.verses.value.indices) return

        val waitMs = durationForPage(fromIndex)
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
        val turningOn = !_isAutoPlaying.value
        _isAutoPlaying.value = turningOn
        if (turningOn) {
            scope.launch {
                toastEvent.emit("Auto Play is on: The next stanza will move on its own")
            }
            if (currentPageIndex >= 0) scheduleAutoAdvance(currentPageIndex)
        } else {
            autoPlayJob?.cancel()
        }
    }

    fun loadDurations(songId: Int) {
        scope.launch {
            songDurations = autoPlayRepo.getDurations(songId)
            if (_isAutoPlaying.value && currentPageIndex >= 0) {
                scheduleAutoAdvance(currentPageIndex)
            }
        }
    }

    fun resetForNewSong() {
        autoPlayJob?.cancel()
        songDurations = null
        currentPageIndex = -1
        pageEnteredAt = 0L
        pendingAutoAdvanceIndex = null
        lastAutoAdvanceFromIndex = null
        lastAutoAdvanceFromAt = 0L
    }

    fun onVerseIndexChanged(index: Int) {
        castingRepo.updateIndex(index)

        val now = System.currentTimeMillis()
        val previousIndex = currentPageIndex
        val wasAutoAdvance = pendingAutoAdvanceIndex == index
        pendingAutoAdvanceIndex = null

        if (previousIndex >= 0 && previousIndex != index && pageEnteredAt > 0 && !wasAutoAdvance) {
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

        if (_isAutoPlaying.value) scheduleAutoAdvance(index)
    }

    fun cancel() {
        autoPlayJob?.cancel()
    }
}
