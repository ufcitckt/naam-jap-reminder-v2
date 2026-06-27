package com.naamjap.reminder

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ChantLogDao {
    @Query("SELECT * FROM chant_logs ORDER BY timestamp DESC")
    fun getAllLogs(): Flow<List<ChantLog>>

    @Insert
    suspend fun insertLog(log: ChantLog)

    @Query("DELETE FROM chant_logs")
    suspend fun clearAllLogs()

    @Query("SELECT COUNT(*) FROM chant_logs WHERE type = 'MALA'")
    fun getMalaChantCount(): Flow<Int>
}
