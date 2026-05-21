package com.krishnajeena.persona.data_layer

import androidx.room.Insert
import androidx.room.Dao
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FocusSessionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertSession(session: FocusSession)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertSessions(sessions: List<FocusSession>)

    @Query("SELECT * FROM focus_sessions WHERE userId = :userId ORDER BY startTimeMs DESC")
    fun observeAllSessions(userId: String): Flow<List<FocusSession>>

    @Query("SELECT * FROM focus_sessions WHERE userId = :userId AND status = :status ORDER BY startTimeMs DESC")
    fun observeSessionsByStatus(userId: String, status: String): Flow<List<FocusSession>>

    @Query("SELECT * FROM focus_sessions WHERE sessionId = :sessionId LIMIT 1")
    suspend fun getById(sessionId: String): FocusSession?

    @Query("SELECT * FROM focus_sessions WHERE userId = :userId AND status = 'IN_PROGRESS' ORDER BY startTimeMs DESC")
    suspend fun getInProgressSessions(userId: String): List<FocusSession>

    @Query(
        "UPDATE focus_sessions " +
            "SET userId = :newUserId, username = :newUsername, syncStatus = :syncStatus, updatedAtMs = :updatedAtMs, syncedAtMs = NULL " +
            "WHERE userId = :oldUserId"
    )
    suspend fun migrateUserSessions(
        oldUserId: String,
        newUserId: String,
        newUsername: String,
        syncStatus: String,
        updatedAtMs: Long
    )

    @Query("SELECT SUM(actualDurationMinutes) FROM focus_sessions WHERE userId = :userId AND status = 'COMPLETED'")
    suspend fun getTotalCompletedMinutes(userId: String): Int?

    @Query("SELECT SUM(actualDurationMinutes) FROM focus_sessions WHERE userId = :userId AND date = :date AND status = 'COMPLETED'")
    suspend fun getTotalCompletedMinutesForDate(userId: String, date: String): Int?

    @Query("SELECT COUNT(*) FROM focus_sessions WHERE userId = :userId AND date = :date AND status = 'COMPLETED'")
    suspend fun getCompletedSessionCountForDate(userId: String, date: String): Int

    @Query("SELECT * FROM focus_sessions WHERE userId = :userId AND syncStatus != 'SYNCED' AND status != 'IN_PROGRESS' ORDER BY updatedAtMs ASC")
    suspend fun getPendingSyncSessions(userId: String): List<FocusSession>

    @Query("UPDATE focus_sessions SET syncStatus = :syncStatus, syncedAtMs = :syncedAtMs, updatedAtMs = :updatedAtMs WHERE sessionId = :sessionId")
    suspend fun updateSyncStatus(sessionId: String, syncStatus: String, syncedAtMs: Long?, updatedAtMs: Long)

    @Query("DELETE FROM focus_sessions WHERE userId = :userId AND status = 'IN_PROGRESS'")
    suspend fun deleteAllInProgress(userId: String)
}
