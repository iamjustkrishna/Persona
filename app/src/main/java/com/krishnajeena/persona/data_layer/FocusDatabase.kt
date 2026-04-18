package com.krishnajeena.persona.data_layer

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [FocusSession::class],
    version = 1,
    exportSchema = false
)
abstract class FocusDatabase : RoomDatabase() {
    abstract fun focusSessionDao(): FocusSessionDao

    companion object {
        @Volatile
        private var INSTANCE: FocusDatabase? = null

        fun getInstance(context: Context): FocusDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    FocusDatabase::class.java,
                    "focus_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
