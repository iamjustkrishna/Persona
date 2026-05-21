package com.krishnajeena.persona.data_layer

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [FocusSession::class],
    version = 2,
    exportSchema = false
)
abstract class FocusDatabase : RoomDatabase() {
    abstract fun focusSessionDao(): FocusSessionDao

    companion object {
        @Volatile
        private var INSTANCE: FocusDatabase? = null

        private val MIGRATION_1_2 = object : androidx.room.migration.Migration(1, 2) {
            override fun migrate(db: androidx.sqlite.db.SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `focus_sessions_new` (
                        `sessionId` TEXT NOT NULL,
                        `userId` TEXT NOT NULL,
                        `username` TEXT NOT NULL,
                        `startTimeMs` INTEGER NOT NULL,
                        `endTimeMs` INTEGER,
                        `plannedDurationMinutes` INTEGER NOT NULL,
                        `actualDurationMinutes` INTEGER NOT NULL,
                        `status` TEXT NOT NULL,
                        `abandonedReason` TEXT,
                        `sessionType` TEXT NOT NULL,
                        `date` TEXT NOT NULL,
                        `withMusic` INTEGER NOT NULL,
                        `syncStatus` TEXT NOT NULL,
                        `createdAtMs` INTEGER NOT NULL,
                        `updatedAtMs` INTEGER NOT NULL,
                        `syncedAtMs` INTEGER,
                        PRIMARY KEY(`sessionId`)
                    )
                    """.trimIndent()
                )

                // Best-effort migration from v1 rows.
                db.execSQL(
                    """
                    INSERT INTO focus_sessions_new (
                        sessionId, userId, username,
                        startTimeMs, endTimeMs,
                        plannedDurationMinutes, actualDurationMinutes,
                        status, abandonedReason,
                        sessionType, date, withMusic,
                        syncStatus, createdAtMs, updatedAtMs, syncedAtMs
                    )
                    SELECT
                        'legacy_' || id || '_' || startTime,
                        '',
                        'Unknown',
                        startTime,
                        endTime,
                        durationMinutes,
                        durationMinutes,
                        CASE WHEN completed = 1 THEN 'COMPLETED' ELSE 'ABANDONED' END,
                        NULL,
                        sessionType,
                        date,
                        withMusic,
                        'PENDING',
                        startTime,
                        endTime,
                        NULL
                    FROM focus_sessions
                    """.trimIndent()
                )

                db.execSQL("DROP TABLE focus_sessions")
                db.execSQL("ALTER TABLE focus_sessions_new RENAME TO focus_sessions")
            }
        }

        fun getInstance(context: Context): FocusDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    FocusDatabase::class.java,
                    "focus_database"
                )
                    .addMigrations(MIGRATION_1_2)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
