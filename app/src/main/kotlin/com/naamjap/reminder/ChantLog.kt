package com.naamjap.reminder

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chant_logs")
data class ChantLog(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val timestamp: Long,
    val type: String, // "MALA" or "REMINDER"
    val detail: String
)
