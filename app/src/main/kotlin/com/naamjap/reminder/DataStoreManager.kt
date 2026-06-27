package com.naamjap.reminder

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "naam_jap_settings")

class DataStoreManager(private val context: Context) {

    companion object {
        val REMINDER_INTERVAL_MINS = intPreferencesKey("reminder_interval_mins")
        val SHOW_NOTIFICATION = booleanPreferencesKey("show_notification")
        val PLAY_SOUND = booleanPreferencesKey("play_sound")
        val SOUND_TYPE = stringPreferencesKey("sound_type") // "gong", "bell", "tts", "none"
        val QUIET_HOURS_ENABLED = booleanPreferencesKey("quiet_hours_enabled")
        val QUIET_HOURS_START = stringPreferencesKey("quiet_hours_start")
        val QUIET_HOURS_END = stringPreferencesKey("quiet_hours_end")
        val MALA_COMPLETED_BEADS = intPreferencesKey("mala_completed_beads")
        val MALA_TOTAL_ROUNDS = intPreferencesKey("mala_total_rounds")
    }

    val intervalFlow: Flow<Int> = context.dataStore.data.map { preferences ->
        preferences[REMINDER_INTERVAL_MINS] ?: 15
    }

    val showNotificationFlow: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[SHOW_NOTIFICATION] ?: true
    }

    val playSoundFlow: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[PLAY_SOUND] ?: true
    }

    val soundTypeFlow: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[SOUND_TYPE] ?: "gong"
    }

    val quietHoursEnabledFlow: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[QUIET_HOURS_ENABLED] ?: false
    }

    val quietHoursStartFlow: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[QUIET_HOURS_START] ?: "22:00"
    }

    val quietHoursEndFlow: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[QUIET_HOURS_END] ?: "06:00"
    }

    val malaCompletedBeadsFlow: Flow<Int> = context.dataStore.data.map { preferences ->
        preferences[MALA_COMPLETED_BEADS] ?: 0
    }

    val malaTotalRoundsFlow: Flow<Int> = context.dataStore.data.map { preferences ->
        preferences[MALA_TOTAL_ROUNDS] ?: 0
    }

    suspend fun saveInterval(mins: Int) {
        context.dataStore.edit { preferences ->
            preferences[REMINDER_INTERVAL_MINS] = mins
        }
    }

    suspend fun saveShowNotification(show: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[SHOW_NOTIFICATION] = show
        }
    }

    suspend fun savePlaySound(play: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PLAY_SOUND] = play
        }
    }

    suspend fun saveSoundType(type: String) {
        context.dataStore.edit { preferences ->
            preferences[SOUND_TYPE] = type
        }
    }

    suspend fun saveQuietHoursEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[QUIET_HOURS_ENABLED] = enabled
        }
    }

    suspend fun saveQuietHoursStart(time: String) {
        context.dataStore.edit { preferences ->
            preferences[QUIET_HOURS_START] = time
        }
    }

    suspend fun saveQuietHoursEnd(time: String) {
        context.dataStore.edit { preferences ->
            preferences[QUIET_HOURS_END] = time
        }
    }

    suspend fun saveMalaCompletedBeads(beads: Int) {
        context.dataStore.edit { preferences ->
            preferences[MALA_COMPLETED_BEADS] = beads
        }
    }

    suspend fun saveMalaTotalRounds(rounds: Int) {
        context.dataStore.edit { preferences ->
            preferences[MALA_TOTAL_ROUNDS] = rounds
        }
    }
}
