package com.krishnajeena.persona.data_layer

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "focus_sessions")
data class FocusSession(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val startTime: Long,
    val endTime: Long,
    val durationMinutes: Int,
    val completed: Boolean,
    val sessionType: String = "focus", // "focus", "short_break", "long_break"
    val date: String, // YYYY-MM-DD format for easy grouping
    val withMusic: Boolean = false
)

data class DailyFocusStats(
    val date: String,
    val totalMinutes: Int,
    val sessionsCompleted: Int,
    val streak: Int
)

data class FocusComparison(
    val userTotalMinutes: Int,
    val attentionSpanComparison: String, // "You have 5x better attention than a goldfish"
    val percentileRank: Int, // 90 means better than 90% of users
    val comparisonText: String // "Better than 90% of users"
)

data class LeaderboardEntry(
    val username: String,
    val totalMinutes: Int,
    val rank: Int,
    val isCurrentUser: Boolean = false
)
