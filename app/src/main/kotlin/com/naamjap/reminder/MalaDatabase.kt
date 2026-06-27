package com.naamjap.reminder

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [ChantLog::class], version = 1, exportSchema = false)
abstract class MalaDatabase : RoomDatabase() {
    abstract fun chantLogDao(): ChantLogDao

    companion object {
        @Volatile
        private var INSTANCE: MalaDatabase? = null

        fun getDatabase(context: Context): MalaDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MalaDatabase::class.java,
                    "naam_jap_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
