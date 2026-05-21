package com.krishnajeena.persona.other

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.hilt.android.EntryPointAccessors

class FocusSyncWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val entryPoint = EntryPointAccessors.fromApplication(
            applicationContext,
            FocusSyncWorkerEntryPoint::class.java
        )

        val firebaseAuth = entryPoint.firebaseAuth()
        val repository = entryPoint.focusRepository()

        val user = firebaseAuth.currentUser ?: run {
            Log.d("FocusSync", "Worker: no Firebase user; skipping")
            return Result.success()
        }
        if (user.isAnonymous) {
            Log.d("FocusSync", "Worker: anonymous user; skipping")
            return Result.success()
        }

        val userId = user.uid
        return try {
            repository.syncPendingSessions(userId)
            Result.success()
        } catch (e: Exception) {
            Log.e("FocusSync", "Worker sync failed for user=$userId", e)
            Result.retry()
        }
    }
}
