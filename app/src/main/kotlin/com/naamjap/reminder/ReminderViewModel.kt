package com.naamjap.reminder

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class ReminderViewModel(application: Application) : AndroidViewModel(application) {

    private val dataStoreManager = DataStoreManager(application)
    private val database = MalaDatabase.getDatabase(application)
    private val dao = database.chantLogDao()
    private val workManager = WorkManager.getInstance(application)

    val interval: StateFlow<Int> = dataStoreManager.intervalFlow.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), 15
    )
    val showNotification: StateFlow<Boolean> = dataStoreManager.showNotificationFlow.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), true
    )
    val playSound: StateFlow<Boolean> = dataStoreManager.playSoundFlow.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), true
    )
    val soundType: StateFlow<String> = dataStoreManager.soundTypeFlow.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), "gong"
    )
    val quietHoursEnabled: StateFlow<Boolean> = dataStoreManager.quietHoursEnabledFlow.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), false
    )
    val quietHoursStart: StateFlow<String> = dataStoreManager.quietHoursStartFlow.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), "22:00"
    )
    val quietHoursEnd: StateFlow<String> = dataStoreManager.quietHoursEndFlow.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), "06:00"
    )
    val malaCompletedBeads: StateFlow<Int> = dataStoreManager.malaCompletedBeadsFlow.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), 0
    )
    val malaTotalRounds: StateFlow<Int> = dataStoreManager.malaTotalRoundsFlow.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), 0
    )

    val chantLogs: StateFlow<List<ChantLog>> = dao.getAllLogs().stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )

    init {
        scheduleWork(15)
    }

    fun updateInterval(mins: Int) {
        viewModelScope.launch {
            dataStoreManager.saveInterval(mins)
            scheduleWork(mins)
        }
    }

    fun updateShowNotification(show: Boolean) {
        viewModelScope.launch {
            dataStoreManager.saveShowNotification(show)
        }
    }

    fun updatePlaySound(play: Boolean) {
        viewModelScope.launch {
            dataStoreManager.savePlaySound(play)
        }
    }

    fun updateSoundType(type: String) {
        viewModelScope.launch {
            dataStoreManager.saveSoundType(type)
        }
    }

    fun updateQuietHoursEnabled(enabled: Boolean) {
        viewModelScope.launch {
            dataStoreManager.saveQuietHoursEnabled(enabled)
        }
    }

    fun updateQuietHoursStart(time: String) {
        viewModelScope.launch {
            dataStoreManager.saveQuietHoursStart(time)
        }
    }

    fun updateQuietHoursEnd(time: String) {
        viewModelScope.launch {
            dataStoreManager.saveQuietHoursEnd(time)
        }
    }

    fun chantBead() {
        viewModelScope.launch {
            val currentBeads = malaCompletedBeads.value + 1
            if (currentBeads >= 108) {
                val currentRounds = malaTotalRounds.value + 1
                dataStoreManager.saveMalaCompletedBeads(0)
                dataStoreManager.saveMalaTotalRounds(currentRounds)
                dao.insertLog(ChantLog(
                    timestamp = System.currentTimeMillis(),
                    type = "MALA",
                    detail = "Completed 1 full Mala round ($currentRounds total)"
                ))
            } else {
                dataStoreManager.saveMalaCompletedBeads(currentBeads)
                if (currentBeads % 10 == 0) {
                    dao.insertLog(ChantLog(
                        timestamp = System.currentTimeMillis(),
                        type = "MALA",
                        detail = "Chanted $currentBeads beads of current round"
                    ))
                }
            }
        }
    }

    fun resetMala() {
        viewModelScope.launch {
            dataStoreManager.saveMalaCompletedBeads(0)
            dataStoreManager.saveMalaTotalRounds(0)
            dao.insertLog(ChantLog(
                timestamp = System.currentTimeMillis(),
                type = "MALA",
                detail = "Mala progress manually reset"
            ))
        }
    }

    fun clearLogs() {
        viewModelScope.launch {
            dao.clearAllLogs()
        }
    }

    fun triggerTestReminder() {
        val testWorkRequest = OneTimeWorkRequestBuilder<ReminderWorker>().build()
        workManager.enqueue(testWorkRequest)
    }

    private fun scheduleWork(mins: Int) {
        val workRequest = PeriodicWorkRequestBuilder<ReminderWorker>(
            mins.toLong().coerceAtLeast(15),
            TimeUnit.MINUTES
        ).build()

        workManager.enqueueUniquePeriodicWork(
            "naam_jap_periodic_work",
            ExistingPeriodicWorkPolicy.UPDATE,
            workRequest
        )
    }
}
