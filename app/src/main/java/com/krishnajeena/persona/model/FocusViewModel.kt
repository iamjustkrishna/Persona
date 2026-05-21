package com.krishnajeena.persona.model

import android.content.ComponentName
import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.MoreExecutors
import com.google.firebase.auth.FirebaseAuth
import com.krishnajeena.persona.auth.GoogleAuthUiClient
import com.krishnajeena.persona.data_layer.DailyFocusStats
import com.krishnajeena.persona.data_layer.FocusComparison
import com.krishnajeena.persona.data_layer.FocusRepository
import com.krishnajeena.persona.data_layer.FocusSession
import com.krishnajeena.persona.data_layer.FocusSessionStatus
import com.krishnajeena.persona.data_layer.LeaderboardEntry
import kotlinx.coroutines.flow.update
import kotlin.math.ceil
import com.krishnajeena.persona.data_layer.RadioLibrary
import com.krishnajeena.persona.services.RadioService
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import javax.inject.Inject

enum class TimerState {
    IDLE, RUNNING, PAUSED, COMPLETED
}

data class TopPopupState(
    val title: String,
    val message: String,
    val kind: Kind
) {
    enum class Kind { TOP_30, TOP_10 }
}

@HiltViewModel
class FocusViewModel @Inject constructor(
    private val repository: FocusRepository,
    private val firebaseAuth: FirebaseAuth,
    private val googleAuthUiClient: GoogleAuthUiClient,
    @ApplicationContext private val context: Context
) : ViewModel() {

    var timerState by mutableStateOf(TimerState.IDLE)
        private set

    var selectedDuration by mutableStateOf(25)
        private set

    var remainingSeconds by mutableStateOf(25 * 60)
        private set

    var withMusic by mutableStateOf(false)
        private set

    private var timerJob: Job? = null
    private var quoteJob: Job? = null
    private var activeSessionId: String? = null

    // Music
    private var mediaController: MediaController? = null
    private val focusMusicUrl = RadioLibrary.focusStations.first().streamUrl

    // Sessions (for calendar/history UI)
    private val _allSessions = MutableStateFlow<List<FocusSession>>(emptyList())
    val allSessions: StateFlow<List<FocusSession>> = _allSessions.asStateFlow()

    // Stats
    private val _todayMinutes = MutableStateFlow(0)
    val todayMinutes: StateFlow<Int> = _todayMinutes.asStateFlow()

    private val _weeklyStats = MutableStateFlow<List<DailyFocusStats>>(emptyList())
    val weeklyStats: StateFlow<List<DailyFocusStats>> = _weeklyStats.asStateFlow()

    private val _totalMinutes = MutableStateFlow(0)
    val totalMinutes: StateFlow<Int> = _totalMinutes.asStateFlow()

    private val _focusComparison = MutableStateFlow<FocusComparison?>(null)
    val focusComparison: StateFlow<FocusComparison?> = _focusComparison.asStateFlow()

    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    private val _topPopup = MutableStateFlow<TopPopupState?>(null)
    val topPopup: StateFlow<TopPopupState?> = _topPopup.asStateFlow()

    // Leaderboards
    private val _leaderboard = MutableStateFlow<List<LeaderboardEntry>>(emptyList())
    val leaderboard: StateFlow<List<LeaderboardEntry>> = _leaderboard.asStateFlow()

    private val _allTimeLeaderboard = MutableStateFlow<List<LeaderboardEntry>>(emptyList())
    val allTimeLeaderboard: StateFlow<List<LeaderboardEntry>> = _allTimeLeaderboard.asStateFlow()

    private val _currentStreak = MutableStateFlow(0)
    val currentStreak: StateFlow<Int> = _currentStreak.asStateFlow()

    // Quotes
    var currentMotivationalQuote by mutableStateOf("")
        private set

    private val motivationalQuotes = listOf(
        "Stay focused, you've got this! 💪",
        "One step at a time 🎯",
        "Deep work creates deep results 🌟",
        "Your future self will thank you 🚀",
        "Focus is a superpower ⚡",
        "Every minute counts 📈",
        "You're doing amazing! 🌈",
        "Consistency beats intensity 🔥",
        "Great things take time ⏳",
        "Your dedication inspires 💎"
    )

    private var activeUserId: String = "local"
    private var activeUsername: String = "You"

    private var localCollectorJob: Job? = null
    private var cloudCollectorJob: Job? = null
    private var dailyLeaderboardJob: Job? = null
    private var totalLeaderboardJob: Job? = null

    private var wasTop30Today: Boolean = false

    private val authListener = FirebaseAuth.AuthStateListener {
        refreshUserContext()
    }

    init {
        initializeMusicController()
        firebaseAuth.addAuthStateListener(authListener)
        refreshUserContext()
    }

    private fun isRealLoggedIn(): Boolean {
        val user = firebaseAuth.currentUser ?: return false
        return !user.isAnonymous
    }

    private fun refreshUserContext() {
        val loggedIn = isRealLoggedIn()
        _isLoggedIn.value = loggedIn

        val newUserId = if (loggedIn) firebaseAuth.currentUser!!.uid else "local"
        val newUsername = if (loggedIn) (firebaseAuth.currentUser?.displayName ?: "You") else "You"

        // If we just signed in, migrate local sessions over to this UID.
        if (activeUserId == "local" && newUserId != "local") {
            viewModelScope.launch {
                repository.migrateLocalSessionsToUser(newUserId, newUsername)
            }
        }

        activeUserId = newUserId
        activeUsername = newUsername

        restartCollectors()
    }

    private fun restartCollectors() {
        localCollectorJob?.cancel()
        cloudCollectorJob?.cancel()
        dailyLeaderboardJob?.cancel()
        totalLeaderboardJob?.cancel()

        // Clear leaderboards when logged out
        if (!_isLoggedIn.value) {
            _leaderboard.value = emptyList()
            _allTimeLeaderboard.value = emptyList()
            wasTop30Today = false
        }

        // Finalize any stale IN_PROGRESS sessions for the active user.
        // Don't do this while a timer is actively running (it would incorrectly abandon it).
        if (timerState == TimerState.IDLE && activeSessionId == null) {
            viewModelScope.launch {
                repository.abandonStaleInProgressSessions(activeUserId)
            }
        }

        // Local sessions feed (calendar/history)
        localCollectorJob = viewModelScope.launch {
            repository.observeLocalSessions(activeUserId).collect { sessions ->
                _allSessions.value = sessions
                recomputeStats(sessions)
            }
        }

        if (!_isLoggedIn.value) return

        // Cloud sessions -> cache into Room
        cloudCollectorJob = viewModelScope.launch {
            repository.observeCloudSessions(activeUserId).collect { cloudSessions ->
                repository.cacheCloudSessionsToLocal(activeUserId, cloudSessions)
            }
        }
        repository.scheduleSyncWork()

        val today = getTodayDate()
        dailyLeaderboardJob = viewModelScope.launch {
            repository.observeDailyLeaderboard(today, activeUserId).collect { list ->
                _leaderboard.value = list
                maybeShowTopPopup(list)
            }
        }
        totalLeaderboardJob = viewModelScope.launch {
            repository.observeTotalLeaderboard(activeUserId).collect { _allTimeLeaderboard.value = it }
        }
    }

    private fun maybeShowTopPopup(list: List<LeaderboardEntry>) {
        val me = list.firstOrNull { it.isCurrentUser } ?: run {
            wasTop30Today = false
            return
        }

        val threshold = maxOf(1, ceil(list.size * 0.3).toInt())
        val inTop30 = me.rank <= threshold

        if (inTop30 && !wasTop30Today) {
            val kind = if (me.rank <= maxOf(1, ceil(list.size * 0.1).toInt())) {
                TopPopupState.Kind.TOP_10
            } else {
                TopPopupState.Kind.TOP_30
            }

            _topPopup.value = TopPopupState(
                title = if (kind == TopPopupState.Kind.TOP_10) "Top 10%!" else "Top 30%!",
                message = "You're on today's leaderboard (#${me.rank}). Keep it up.",
                kind = kind
            )

            // Auto-hide
            viewModelScope.launch {
                delay(3500)
                _topPopup.update { null }
            }
        }

        wasTop30Today = inTop30
    }

    private fun initializeMusicController() {
        val sessionToken = SessionToken(context, ComponentName(context, RadioService::class.java))
        val controllerFuture = MediaController.Builder(context, sessionToken).buildAsync()
        controllerFuture.addListener({
            mediaController = controllerFuture.get()
        }, MoreExecutors.directExecutor())
    }

    private fun playFocusMusic() {
        mediaController?.let { controller ->
            controller.setMediaItem(MediaItem.fromUri(focusMusicUrl))
            controller.prepare()
            controller.play()
        }
    }

    private fun stopFocusMusic() {
        mediaController?.let { controller ->
            controller.pause()
            controller.clearMediaItems()
        }
    }


    fun setDuration(minutes: Int) {
        if (timerState == TimerState.IDLE) {
            selectedDuration = minutes
            remainingSeconds = minutes * 60
        }
    }

    fun toggleMusic() {
        withMusic = !withMusic
    }

    fun startTimer() {
        if (timerState != TimerState.IDLE && timerState != TimerState.PAUSED) return

        timerState = TimerState.RUNNING

        // Create an in-progress session once per run.
        if (activeSessionId == null) {
            viewModelScope.launch {
                val session = repository.startSession(
                    userId = activeUserId,
                    username = activeUsername,
                    plannedMinutes = selectedDuration,
                    withMusic = withMusic,
                    sessionType = "focus"
                )
                activeSessionId = session.sessionId
            }
        }

        if (withMusic) playFocusMusic()
        startCountdown()
        startQuoteRotation()
    }

    fun pauseTimer() {
        if (timerState != TimerState.RUNNING) return
        timerState = TimerState.PAUSED
        timerJob?.cancel()
        quoteJob?.cancel()
        currentMotivationalQuote = ""
        if (withMusic) stopFocusMusic()
    }

    fun resetTimer() {
        // If the user resets while a session exists, mark it abandoned.
        val sessionId = activeSessionId
        if (sessionId != null && (timerState == TimerState.RUNNING || timerState == TimerState.PAUSED)) {
            viewModelScope.launch {
                repository.abandonSession(activeUserId, sessionId, reason = "left_early")
            }
        }

        timerJob?.cancel()
        quoteJob?.cancel()
        stopFocusMusic()

        activeSessionId = null
        timerState = TimerState.IDLE
        remainingSeconds = selectedDuration * 60
        currentMotivationalQuote = ""
    }

    private fun startCountdown() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (remainingSeconds > 0 && timerState == TimerState.RUNNING) {
                delay(1000)
                remainingSeconds--
            }
            if (remainingSeconds == 0 && timerState == TimerState.RUNNING) {
                completeSession()
            }
        }
    }

    private fun startQuoteRotation() {
        currentMotivationalQuote = motivationalQuotes.random()
        quoteJob?.cancel()
        quoteJob = viewModelScope.launch {
            delay(15000)
            var shown = 1
            while (timerState == TimerState.RUNNING && shown < 5) {
                currentMotivationalQuote = motivationalQuotes.random()
                delay(20000)
                shown++
            }
            currentMotivationalQuote = ""
        }
    }

    private fun completeSession() {
        timerState = TimerState.COMPLETED
        quoteJob?.cancel()
        currentMotivationalQuote = ""
        stopFocusMusic()

        val sessionId = activeSessionId
        activeSessionId = null

        if (sessionId != null) {
            viewModelScope.launch {
                repository.completeSession(activeUserId, sessionId)
            }
        }
    }

    fun acknowledgeCompletion() {
        resetTimer()
    }

    private fun recomputeStats(sessions: List<FocusSession>) {
        val completed = sessions.filter { it.status == FocusSessionStatus.COMPLETED }
        val today = getTodayDate()

        val todayTotal = completed
            .filter { it.date == today }
            .sumOf { it.actualDurationMinutes }

        val total = completed.sumOf { it.actualDurationMinutes }

        _todayMinutes.value = todayTotal
        _totalMinutes.value = total
        _weeklyStats.value = computeWeeklyStats(completed)
        _currentStreak.value = computeStreak(completed)
        _focusComparison.value = generateComparisons(total)
    }

    private fun computeWeeklyStats(completed: List<FocusSession>): List<DailyFocusStats> {
        val calendar = Calendar.getInstance()
        val stats = mutableListOf<DailyFocusStats>()

        for (i in 6 downTo 0) {
            val c = calendar.clone() as Calendar
            c.add(Calendar.DAY_OF_YEAR, -i)
            val dateStr = formatDate(c.time)

            val mins = completed.filter { it.date == dateStr }.sumOf { it.actualDurationMinutes }
            val sessionsCount = completed.count { it.date == dateStr }

            stats.add(
                DailyFocusStats(
                    date = dateStr,
                    totalMinutes = mins,
                    sessionsCompleted = sessionsCount,
                    streak = 0
                )
            )
        }
        return stats
    }

    private fun computeStreak(completed: List<FocusSession>): Int {
        val calendar = Calendar.getInstance()
        var streak = 0

        while (true) {
            val dateStr = formatDate(calendar.time)
            val mins = completed.filter { it.date == dateStr }.sumOf { it.actualDurationMinutes }
            if (mins > 0) {
                streak++
                calendar.add(Calendar.DAY_OF_YEAR, -1)
            } else {
                break
            }
        }
        return streak
    }

    private fun generateComparisons(totalMinutes: Int): FocusComparison {
        val goldfishMultiplier = (totalMinutes.toFloat() / 0.15f).toInt()
        val percentile = when {
            totalMinutes < 100 -> 30
            totalMinutes < 300 -> 50
            totalMinutes < 600 -> 70
            totalMinutes < 1200 -> 85
            totalMinutes < 2400 -> 92
            else -> 98
        }

        return FocusComparison(
            userTotalMinutes = totalMinutes,
            attentionSpanComparison = when {
                goldfishMultiplier < 10 -> "You're building focus! 🐠"
                goldfishMultiplier < 100 -> "You have ${goldfishMultiplier}x better attention than a goldfish! 🐟"
                goldfishMultiplier < 500 -> "Your focus is legendary! 🦅 ${goldfishMultiplier}x goldfish!"
                else -> "Superhuman focus! 🧠 ${goldfishMultiplier}x goldfish!"
            },
            percentileRank = percentile,
            comparisonText = when {
                percentile < 50 -> "Keep going! You're building momentum 💪"
                percentile < 70 -> "Better than $percentile% of users! 🚀"
                percentile < 90 -> "Top $percentile%! You're crushing it! 🔥"
                else -> "Top $percentile%! Elite focus master! 👑"
            }
        )
    }

    fun getProgressPercentage(): Float {
        val totalSeconds = selectedDuration * 60
        return ((totalSeconds - remainingSeconds).toFloat() / totalSeconds)
    }

    fun getFormattedTime(): String {
        val minutes = remainingSeconds / 60
        val seconds = remainingSeconds % 60
        return String.format("%02d:%02d", minutes, seconds)
    }

    private fun getTodayDate(): String =
        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

    private fun formatDate(date: Date): String =
        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(date)

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
        quoteJob?.cancel()
        stopFocusMusic()
        mediaController?.release()

        // Best-effort: mark current session abandoned if ViewModel is destroyed mid-run.
        val sessionId = activeSessionId
        if (sessionId != null && (timerState == TimerState.RUNNING || timerState == TimerState.PAUSED)) {
            viewModelScope.launch {
                repository.abandonSession(activeUserId, sessionId, reason = "app_closed")
            }
        }
    }
}
