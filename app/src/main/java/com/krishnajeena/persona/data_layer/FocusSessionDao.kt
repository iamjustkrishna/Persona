package com.krishnajeena.persona.data_layer

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Dao
interface FocusSessionDao {

    @Insert
    suspend fun insertSession(session: FocusSession)

    @Query("SELECT * FROM focus_sessions WHERE completed = 1 ORDER BY startTime DESC")
    fun getAllCompletedSessions(): Flow<List<FocusSession>>

    @Query("SELECT * FROM focus_sessions WHERE date = :date AND completed = 1")
    suspend fun getSessionsForDate(date: String): List<FocusSession>

    @Query("SELECT SUM(durationMinutes) FROM focus_sessions WHERE completed = 1")
    suspend fun getTotalFocusMinutes(): Int?

    @Query("SELECT SUM(durationMinutes) FROM focus_sessions WHERE date = :date AND completed = 1")
    suspend fun getTotalFocusMinutesForDate(date: String): Int?

    @Query("SELECT COUNT(*) FROM focus_sessions WHERE date = :date AND completed = 1")
    suspend fun getSessionCountForDate(date: String): Int

    // Get last 7 days of sessions
    @Query("SELECT * FROM focus_sessions WHERE date >= :startDate AND completed = 1 ORDER BY date DESC")
    suspend fun getSessionsForDateRange(startDate: String): List<FocusSession>

    // Get today's total minutes
    @Query("SELECT SUM(durationMinutes) FROM focus_sessions WHERE date = :today AND completed = 1")
    fun getTodayFocusMinutes(today: String): Flow<Int?>

    @Query("DELETE FROM focus_sessions")
    suspend fun deleteAllSessions()
}
