package com.krishnajeena.persona.data_layer

import android.content.Context
import android.util.Log
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.OneTimeWorkRequest
import androidx.work.ExistingWorkPolicy
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FocusRepository @Inject constructor(
    private val focusSessionDao: FocusSessionDao,
    private val firestore: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth,
    @ApplicationContext private val context: Context
) {
    private val workManager by lazy { WorkManager.getInstance(context) }

    fun observeLocalSessions(userId: String): Flow<List<FocusSession>> =
        focusSessionDao.observeAllSessions(userId)

    /**
     * Firestore realtime sessions feed. ViewModel should collect and cache to Room.
     */
    fun observeCloudSessions(userId: String): Flow<List<FocusSession>> = callbackFlow {
        val reg = userSessionsCollection(userId)
            .orderBy("startTimeMs", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val sessions = snapshot?.documents?.mapNotNull { doc ->
                    val sessionId = doc.getString("sessionId") ?: doc.id
                    val username = doc.getString("username") ?: "Unknown"
                    val startTimeMs = doc.getLong("startTimeMs") ?: return@mapNotNull null
                    val endTimeMs = doc.getLong("endTimeMs")
                    val plannedDurationMinutes = (doc.getLong("plannedDurationMinutes") ?: 0L).toInt()
                    val actualDurationMinutes = (doc.getLong("actualDurationMinutes") ?: 0L).toInt()
                    val status = doc.getString("status") ?: FocusSessionStatus.COMPLETED
                    val abandonedReason = doc.getString("abandonedReason")
                    val sessionType = doc.getString("sessionType") ?: "focus"
                    val date = doc.getString("date") ?: formatDate(Date(startTimeMs))
                    val withMusic = doc.getBoolean("withMusic") ?: false
                    val createdAtMs = doc.getLong("createdAtMs") ?: startTimeMs
                    val updatedAtMs = doc.getLong("updatedAtMs") ?: createdAtMs

                    FocusSession(
                        sessionId = sessionId,
                        userId = userId,
                        username = username,
                        startTimeMs = startTimeMs,
                        endTimeMs = endTimeMs,
                        plannedDurationMinutes = plannedDurationMinutes,
                        actualDurationMinutes = actualDurationMinutes,
                        status = status,
                        abandonedReason = abandonedReason,
                        sessionType = sessionType,
                        date = date,
                        withMusic = withMusic,
                        syncStatus = SyncStatus.SYNCED,
                        createdAtMs = createdAtMs,
                        updatedAtMs = updatedAtMs,
                        syncedAtMs = System.currentTimeMillis()
                    )
                } ?: emptyList()

                trySend(sessions)
            }

        awaitClose { reg.remove() }
    }

    fun observeDailyLeaderboard(date: String, currentUserId: String): Flow<List<LeaderboardEntry>> = callbackFlow {
        val reg = dailyLeaderboardCollection(date)
            .orderBy("scoreMinutes", Query.Direction.DESCENDING)
            .limit(50)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val entries = snapshot?.documents?.mapIndexedNotNull { idx, doc ->
                    val userId = doc.getString("userId") ?: doc.id
                    val username = doc.getString("username") ?: "Unknown"
                    val focusedMinutes = (doc.getLong("focusedMinutes") ?: 0L).toInt()
                    val penaltyMinutes = (doc.getLong("penaltyMinutes") ?: 0L).toInt()
                    val scoreMinutes = (doc.getLong("scoreMinutes") ?: (focusedMinutes - penaltyMinutes).toLong()).toInt()

                    // For the existing UI, treat totalMinutes as the "score" for today's board.
                    LeaderboardEntry(
                        username = username,
                        totalMinutes = scoreMinutes,
                        rank = idx + 1,
                        isCurrentUser = userId == currentUserId,
                        userId = userId,
                        scoreMinutes = scoreMinutes,
                        focusedMinutes = focusedMinutes,
                        penaltyMinutes = penaltyMinutes
                    )
                } ?: emptyList()

                trySend(entries)
            }

        awaitClose { reg.remove() }
    }

    fun observeTotalLeaderboard(currentUserId: String): Flow<List<LeaderboardEntry>> = callbackFlow {
        val reg = totalLeaderboardCollection()
            .orderBy("focusedMinutes", Query.Direction.DESCENDING)
            .limit(50)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val entries = snapshot?.documents?.mapIndexedNotNull { idx, doc ->
                    val userId = doc.getString("userId") ?: doc.id
                    val username = doc.getString("username") ?: "Unknown"
                    val focusedMinutes = (doc.getLong("focusedMinutes") ?: 0L).toInt()
                    val penaltyMinutes = (doc.getLong("penaltyMinutes") ?: 0L).toInt()
                    val scoreMinutes = (doc.getLong("scoreMinutes") ?: (focusedMinutes - penaltyMinutes).toLong()).toInt()

                    LeaderboardEntry(
                        username = username,
                        totalMinutes = focusedMinutes,
                        rank = idx + 1,
                        isCurrentUser = userId == currentUserId,
                        userId = userId,
                        scoreMinutes = scoreMinutes,
                        focusedMinutes = focusedMinutes,
                        penaltyMinutes = penaltyMinutes
                    )
                } ?: emptyList()

                trySend(entries)
            }

        awaitClose { reg.remove() }
    }

    suspend fun startSession(
        userId: String,
        username: String,
        plannedMinutes: Int,
        withMusic: Boolean,
        sessionType: String = "focus"
    ): FocusSession {
        val now = System.currentTimeMillis()
        val sessionId = UUID.randomUUID().toString()
        val session = FocusSession(
            sessionId = sessionId,
            userId = userId,
            username = username,
            startTimeMs = now,
            endTimeMs = null,
            plannedDurationMinutes = plannedMinutes,
            actualDurationMinutes = 0,
            status = FocusSessionStatus.IN_PROGRESS,
            abandonedReason = null,
            sessionType = sessionType,
            date = formatDate(Date(now)),
            withMusic = withMusic,
            syncStatus = SyncStatus.PENDING,
            createdAtMs = now,
            updatedAtMs = now,
            syncedAtMs = null
        )

        focusSessionDao.upsertSession(session)
        // Best-effort immediate sync (does not affect leaderboards yet).
        trySyncNow(userId)
        return session
    }

    suspend fun completeSession(userId: String, sessionId: String) {
        val existing = focusSessionDao.getById(sessionId) ?: return
        val end = System.currentTimeMillis()
        val actualMinutes = ((end - existing.startTimeMs) / 60_000L).toInt().coerceAtLeast(0)

        val updated = existing.copy(
            endTimeMs = end,
            actualDurationMinutes = actualMinutes,
            status = FocusSessionStatus.COMPLETED,
            abandonedReason = null,
            syncStatus = SyncStatus.PENDING,
            updatedAtMs = end,
            syncedAtMs = null
        )

        focusSessionDao.upsertSession(updated)
        trySyncNow(userId)
    }

    suspend fun abandonSession(userId: String, sessionId: String, reason: String) {
        val existing = focusSessionDao.getById(sessionId) ?: return
        val end = System.currentTimeMillis()
        val actualMinutes = ((end - existing.startTimeMs) / 60_000L).toInt().coerceAtLeast(0)

        val updated = existing.copy(
            endTimeMs = end,
            actualDurationMinutes = actualMinutes,
            status = FocusSessionStatus.ABANDONED,
            abandonedReason = reason,
            syncStatus = SyncStatus.PENDING,
            updatedAtMs = end,
            syncedAtMs = null
        )

        focusSessionDao.upsertSession(updated)
        trySyncNow(userId)
    }

    /**
     * If the app/process was killed mid-session, we may have IN_PROGRESS sessions stuck locally.
     * This converts "stale" IN_PROGRESS sessions into ABANDONED so they appear on the calendar
     * (in red) and apply penalty to leaderboards once synced.
     */
    suspend fun abandonStaleInProgressSessions(
        userId: String,
        staleAfterMs: Long = 2 * 60_000L,
        reason: String = "app_closed"
    ) {
        val now = System.currentTimeMillis()
        val inProgress = focusSessionDao.getInProgressSessions(userId)
        if (inProgress.isEmpty()) return

        for (s in inProgress) {
            if (now - s.startTimeMs < staleAfterMs) continue

            val actualMinutes = ((now - s.startTimeMs) / 60_000L).toInt().coerceAtLeast(0)
            val updated = s.copy(
                endTimeMs = now,
                actualDurationMinutes = actualMinutes,
                status = FocusSessionStatus.ABANDONED,
                abandonedReason = reason,
                syncStatus = SyncStatus.PENDING,
                updatedAtMs = now,
                syncedAtMs = null
            )
            focusSessionDao.upsertSession(updated)
        }

        trySyncNow(userId)
    }

    /**
     * Sync any completed/abandoned sessions waiting in the local outbox.
     * Safe to call repeatedly.
     */
    suspend fun syncPendingSessions(userId: String) {
        val pending = focusSessionDao.getPendingSyncSessions(userId)
        if (pending.isEmpty()) return

        for (session in pending) {
            try {
                pushSessionToCloud(userId, session)
                focusSessionDao.updateSyncStatus(
                    sessionId = session.sessionId,
                    syncStatus = SyncStatus.SYNCED,
                    syncedAtMs = System.currentTimeMillis(),
                    updatedAtMs = System.currentTimeMillis()
                )
            } catch (e: Exception) {
                Log.e("FocusSync", "Failed to push session ${session.sessionId} for user=$userId", e)
                // Keep it pending; schedule work for later.
                scheduleSyncWork()
            }
        }
    }

    fun scheduleSyncWork() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val request: OneTimeWorkRequest = OneTimeWorkRequestBuilder<com.krishnajeena.persona.other.FocusSyncWorker>()
            .setConstraints(constraints)
            .build()

        workManager.enqueueUniqueWork(
            "focus-sync",
            ExistingWorkPolicy.KEEP,
            request
        )
    }

    suspend fun cacheCloudSessionsToLocal(userId: String, sessions: List<FocusSession>) {
        if (sessions.isEmpty()) return
        // Mark as synced so they won't re-upload.
        val normalized = sessions.map { it.copy(userId = userId, syncStatus = SyncStatus.SYNCED) }
        focusSessionDao.upsertSessions(normalized)
    }

    /**
     * Moves local (not signed-in) sessions under the real Firebase UID after the user signs in.
     * After migration, we schedule sync so sessions + leaderboards get uploaded.
     */
    suspend fun migrateLocalSessionsToUser(newUserId: String, newUsername: String) {
        if (newUserId.isBlank() || newUserId == "local") return
        focusSessionDao.migrateUserSessions(
            oldUserId = "local",
            newUserId = newUserId,
            newUsername = newUsername,
            syncStatus = SyncStatus.PENDING,
            updatedAtMs = System.currentTimeMillis()
        )
        trySyncNow(newUserId)
    }

    private fun trySyncNow(userId: String) {
        // Requirement: keep sessions local until the user is actually logged in.
        val user = firebaseAuth.currentUser ?: return
        if (user.isAnonymous) return
        if (userId == "local") return

        // Try now; if it fails, WorkManager will retry when network is back.
        scheduleSyncWork()
    }

    private suspend fun pushSessionToCloud(userId: String, session: FocusSession) {
        // Guard: only push completed/abandoned sessions.
        if (session.status == FocusSessionStatus.IN_PROGRESS) return

        val sessionDoc = userSessionsCollection(userId).document(session.sessionId)

        val payload = hashMapOf(
            "sessionId" to session.sessionId,
            "userId" to userId,
            "username" to session.username,
            "startTimeMs" to session.startTimeMs,
            "endTimeMs" to session.endTimeMs,
            "plannedDurationMinutes" to session.plannedDurationMinutes,
            "actualDurationMinutes" to session.actualDurationMinutes,
            "status" to session.status,
            "abandonedReason" to session.abandonedReason,
            "sessionType" to session.sessionType,
            "date" to session.date,
            "withMusic" to session.withMusic,
            "createdAtMs" to session.createdAtMs,
            "updatedAtMs" to session.updatedAtMs
        )

        sessionDoc.set(payload, SetOptions.merge()).await()

        // Update leaderboards idempotently using a transaction.
        // IMPORTANT: Firestore transactions require that *all reads happen before any writes*.
        firestore.runTransaction { txn ->
            val sessionSnap = txn.get(sessionDoc)
            val alreadyCounted = (sessionSnap.getBoolean("countedInLeaderboard") == true)
            if (alreadyCounted) return@runTransaction null

            // Doc refs
            val dailyDoc = dailyLeaderboardCollection(session.date).document(userId)
            val totalDoc = totalLeaderboardCollection().document(userId)

            // Reads (must happen before writes)
            val dailySnap = txn.get(dailyDoc)
            val totalSnap = txn.get(totalDoc)

            val planned = session.plannedDurationMinutes
            val actual = session.actualDurationMinutes

            val focusedDelta = actual
            val penaltyDelta = if (session.status == FocusSessionStatus.ABANDONED) {
                // Penalty grows with unfinished minutes, minimum 5.
                val unfinished = (planned - actual).coerceAtLeast(0)
                maxOf(5, unfinished)
            } else 0
            val scoreDelta = focusedDelta - penaltyDelta

            // Daily leaderboard
            val dailyFocused = (dailySnap.getLong("focusedMinutes") ?: 0L) + focusedDelta
            val dailyPenalty = (dailySnap.getLong("penaltyMinutes") ?: 0L) + penaltyDelta
            val dailyScore = (dailySnap.getLong("scoreMinutes") ?: 0L) + scoreDelta
            val dailyCompleted = (dailySnap.getLong("completedSessions") ?: 0L) + if (session.status == FocusSessionStatus.COMPLETED) 1 else 0
            val dailyAbandoned = (dailySnap.getLong("abandonedSessions") ?: 0L) + if (session.status == FocusSessionStatus.ABANDONED) 1 else 0

            txn.set(
                dailyDoc,
                mapOf(
                    "userId" to userId,
                    "username" to session.username,
                    "focusedMinutes" to dailyFocused,
                    "penaltyMinutes" to dailyPenalty,
                    "scoreMinutes" to dailyScore,
                    "completedSessions" to dailyCompleted,
                    "abandonedSessions" to dailyAbandoned,
                    "updatedAtMs" to System.currentTimeMillis()
                ),
                SetOptions.merge()
            )

            // Total leaderboard
            val totalFocused = (totalSnap.getLong("focusedMinutes") ?: 0L) + focusedDelta
            val totalPenalty = (totalSnap.getLong("penaltyMinutes") ?: 0L) + penaltyDelta
            val totalScore = (totalSnap.getLong("scoreMinutes") ?: 0L) + scoreDelta
            val totalCompleted = (totalSnap.getLong("completedSessions") ?: 0L) + if (session.status == FocusSessionStatus.COMPLETED) 1 else 0
            val totalAbandoned = (totalSnap.getLong("abandonedSessions") ?: 0L) + if (session.status == FocusSessionStatus.ABANDONED) 1 else 0

            txn.set(
                totalDoc,
                mapOf(
                    "userId" to userId,
                    "username" to session.username,
                    "focusedMinutes" to totalFocused,
                    "penaltyMinutes" to totalPenalty,
                    "scoreMinutes" to totalScore,
                    "completedSessions" to totalCompleted,
                    "abandonedSessions" to totalAbandoned,
                    "updatedAtMs" to System.currentTimeMillis()
                ),
                SetOptions.merge()
            )

            txn.update(sessionDoc, "countedInLeaderboard", true)
            null
        }.await()
    }

    private fun userSessionsCollection(userId: String) =
        firestore.collection("users").document(userId).collection("focusSessions")

    private fun dailyLeaderboardCollection(date: String) =
        firestore.collection("leaderboardsDaily").document(date).collection("users")

    private fun totalLeaderboardCollection() =
        firestore.collection("leaderboardsTotal").document("allTime").collection("users")

    private fun formatDate(date: Date): String =
        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(date)
}
