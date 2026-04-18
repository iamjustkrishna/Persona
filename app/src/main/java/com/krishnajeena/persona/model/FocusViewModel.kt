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
import com.krishnajeena.persona.data_layer.DailyFocusStats
import com.krishnajeena.persona.data_layer.FocusComparison
import com.krishnajeena.persona.data_layer.FocusSession
import com.krishnajeena.persona.data_layer.FocusSessionDao
import com.krishnajeena.persona.data_layer.LeaderboardEntry
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
import kotlin.math.min

enum class TimerState {
    IDLE, RUNNING, PAUSED, COMPLETED
}

@HiltViewModel
class FocusViewModel @Inject constructor(
    private val focusSessionDao: FocusSessionDao,
    @ApplicationContext private val context: Context
) : ViewModel() {

    // Timer state
    var timerState by mutableStateOf(TimerState.IDLE)
        private set

    var selectedDuration by mutableStateOf(25) // Default 25 minutes
        private set

    var remainingSeconds by mutableStateOf(25 * 60)
        private set

    var withMusic by mutableStateOf(false)
        private set

    private var timerJob: Job? = null
    private var sessionStartTime: Long = 0

    // Music controller
    private var mediaController: MediaController? = null
    private val focusMusicUrl = RadioLibrary.focusStations.first().streamUrl // Lofi Hip Hop

    // Stats
    private val _todayMinutes = MutableStateFlow(0)
    val todayMinutes: StateFlow<Int> = _todayMinutes.asStateFlow()

    private val _weeklyStats = MutableStateFlow<List<DailyFocusStats>>(emptyList())
    val weeklyStats: StateFlow<List<DailyFocusStats>> = _weeklyStats.asStateFlow()

    private val _totalMinutes = MutableStateFlow(0)
    val totalMinutes: StateFlow<Int> = _totalMinutes.asStateFlow()

    private val _focusComparison = MutableStateFlow<FocusComparison?>(null)
    val focusComparison: StateFlow<FocusComparison?> = _focusComparison.asStateFlow()

    private val _leaderboard = MutableStateFlow<List<LeaderboardEntry>>(emptyList())
    val leaderboard: StateFlow<List<LeaderboardEntry>> = _leaderboard.asStateFlow()

    private val _currentStreak = MutableStateFlow(0)
    val currentStreak: StateFlow<Int> = _currentStreak.asStateFlow()

    // Motivational quotes
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

    private var quoteJob: Job? = null

    init {
        loadStats()
        initializeMusicController()
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
        if (timerState == TimerState.IDLE || timerState == TimerState.PAUSED) {
            timerState = TimerState.RUNNING
            if (sessionStartTime == 0L) {
                sessionStartTime = System.currentTimeMillis()
            }
            if (withMusic) {
                playFocusMusic()
            }
            startCountdown()
            startQuoteRotation()
        }
    }

    fun pauseTimer() {
        if (timerState == TimerState.RUNNING) {
            timerState = TimerState.PAUSED
            timerJob?.cancel()
            quoteJob?.cancel()
            if (withMusic) {
                stopFocusMusic()
            }
        }
    }

    fun resetTimer() {
        timerJob?.cancel()
        quoteJob?.cancel()
        timerState = TimerState.IDLE
        remainingSeconds = selectedDuration * 60
        sessionStartTime = 0
        currentMotivationalQuote = ""
        stopFocusMusic()
    }

    private fun startCountdown() {
        timerJob = viewModelScope.launch {
            while (remainingSeconds > 0 && timerState == TimerState.RUNNING) {
                delay(1000)
                remainingSeconds--
            }
            if (remainingSeconds == 0) {
                completeSession()
            }
        }
    }

    private fun startQuoteRotation() {
        // Show first quote immediately
        currentMotivationalQuote = motivationalQuotes.random()

        quoteJob = viewModelScope.launch {
            delay(15000) // Show first quote for 15 seconds
            var quotesShown = 1

            while (timerState == TimerState.RUNNING && quotesShown < 5) {
                currentMotivationalQuote = motivationalQuotes.random()
                delay(20000) // Show each subsequent quote for 20 seconds
                quotesShown++
            }

            // After showing 5 quotes, clear it
            currentMotivationalQuote = ""
        }
    }

    private fun completeSession() {
        timerState = TimerState.COMPLETED
        quoteJob?.cancel()
        currentMotivationalQuote = ""
        stopFocusMusic()

        val session = FocusSession(
            startTime = sessionStartTime,
            endTime = System.currentTimeMillis(),
            durationMinutes = selectedDuration,
            completed = true,
            date = getTodayDate(),
            withMusic = withMusic
        )

        viewModelScope.launch {
            focusSessionDao.insertSession(session)
            loadStats()
        }
    }

    fun acknowledgeCompletion() {
        resetTimer()
    }

    private fun loadStats() {
        viewModelScope.launch {
            // Load today's minutes
            val today = getTodayDate()
            val todayMins = focusSessionDao.getTotalFocusMinutesForDate(today) ?: 0
            _todayMinutes.value = todayMins

            // Load total minutes
            val total = focusSessionDao.getTotalFocusMinutes() ?: 0
            _totalMinutes.value = total

            // Load weekly stats
            loadWeeklyStats()

            // Calculate streak
            calculateStreak()

            // Generate comparisons
            generateComparisons(total)

            // Generate leaderboard (mock data for now)
            generateLeaderboard(total)
        }
    }

    private suspend fun loadWeeklyStats() {
        val calendar = Calendar.getInstance()
        val stats = mutableListOf<DailyFocusStats>()

        for (i in 6 downTo 0) {
            val date = calendar.clone() as Calendar
            date.add(Calendar.DAY_OF_YEAR, -i)
            val dateString = formatDate(date.time)

            val minutes = focusSessionDao.getTotalFocusMinutesForDate(dateString) ?: 0
            val sessions = focusSessionDao.getSessionCountForDate(dateString)

            stats.add(
                DailyFocusStats(
                    date = dateString,
                    totalMinutes = minutes,
                    sessionsCompleted = sessions,
                    streak = 0
                )
            )
        }

        _weeklyStats.value = stats
    }

    private suspend fun calculateStreak() {
        val calendar = Calendar.getInstance()
        var streak = 0
        var currentDate = calendar.time

        while (true) {
            val dateString = formatDate(currentDate)
            val minutes = focusSessionDao.getTotalFocusMinutesForDate(dateString) ?: 0

            if (minutes > 0) {
                streak++
                calendar.add(Calendar.DAY_OF_YEAR, -1)
                currentDate = calendar.time
            } else {
                break
            }
        }

        _currentStreak.value = streak
    }

    private fun generateComparisons(totalMinutes: Int) {
        // Goldfish has ~9 second attention span
        // Average human deep focus session: 25 minutes
        // User's average session length
        val goldfishMultiplier = (totalMinutes.toFloat() / 0.15f).toInt() // 9 seconds = 0.15 min

        // Mock percentile based on total minutes (in real app, this comes from backend)
        val percentile = when {
            totalMinutes < 100 -> 30
            totalMinutes < 300 -> 50
            totalMinutes < 600 -> 70
            totalMinutes < 1200 -> 85
            totalMinutes < 2400 -> 92
            else -> 98
        }

        val comparison = FocusComparison(
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

        _focusComparison.value = comparison
    }

    private fun generateLeaderboard(userMinutes: Int) {
        // Mock leaderboard data (in production, fetch from Firebase/backend)
        val mockLeaderboard = listOf(
            LeaderboardEntry("FocusMaster", 2400, 1),
            LeaderboardEntry("DeepWorker", 2100, 2),
            LeaderboardEntry("ProductivityKing", 1950, 3),
            LeaderboardEntry("ZenMonk", 1800, 4),
            LeaderboardEntry("You", userMinutes, 5, true),
            LeaderboardEntry("HustleMode", 1500, 6),
            LeaderboardEntry("FlowState", 1350, 7)
        ).sortedByDescending { it.totalMinutes }
            .mapIndexed { index, entry -> entry.copy(rank = index + 1) }

        _leaderboard.value = mockLeaderboard
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

    private fun getTodayDate(): String {
        return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    }

    private fun formatDate(date: Date): String {
        return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(date)
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
        quoteJob?.cancel()
        stopFocusMusic()
        mediaController?.release()
    }
}
