package com.krishnajeena.persona.data_layer

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Local cache + offline-first source of truth.
 * Synced to Firestore when network is available.
 */
@Entity(tableName = "focus_sessions")
data class FocusSession(
    @PrimaryKey
    val sessionId: String,

    val userId: String,
    val username: String,

    val startTimeMs: Long,
    val endTimeMs: Long?,

    val plannedDurationMinutes: Int,
    val actualDurationMinutes: Int,

    /** IN_PROGRESS / COMPLETED / ABANDONED */
    val status: String,

    /** Null when completed. */
    val abandonedReason: String? = null,

    /** "focus", "short_break", "long_break" */
    val sessionType: String = "focus",

    /** YYYY-MM-DD for grouping (calendar/history). */
    val date: String,

    val withMusic: Boolean = false,

    /** PENDING / SYNCED / FAILED */
    val syncStatus: String = SyncStatus.PENDING,

    val createdAtMs: Long,
    val updatedAtMs: Long,
    val syncedAtMs: Long? = null
)

object FocusSessionStatus {
    const val IN_PROGRESS = "IN_PROGRESS"
    const val COMPLETED = "COMPLETED"
    const val ABANDONED = "ABANDONED"
}

object SyncStatus {
    const val PENDING = "PENDING"
    const val SYNCED = "SYNCED"
    const val FAILED = "FAILED"
}

data class DailyFocusStats(
    val date: String,
    val totalMinutes: Int,
    val sessionsCompleted: Int,
    val streak: Int
)

data class FocusComparison(
    val userTotalMinutes: Int,
    val attentionSpanComparison: String,
    val percentileRank: Int,
    val comparisonText: String
)

data class LeaderboardEntry(
    // Backwards-compatible fields used by the current UI
    val username: String,
    val totalMinutes: Int,
    val rank: Int,
    val isCurrentUser: Boolean = false,

    // Extended fields for new leaderboards
    val userId: String = "",
    val scoreMinutes: Int = totalMinutes,
    val focusedMinutes: Int = totalMinutes,
    val penaltyMinutes: Int = 0
)
