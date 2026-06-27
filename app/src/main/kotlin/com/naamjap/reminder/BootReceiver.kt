package com.naamjap.reminder

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            val dataStoreManager = DataStoreManager(context)
            val workManager = WorkManager.getInstance(context)

            CoroutineScope(Dispatchers.IO).launch {
                val intervalMins = dataStoreManager.intervalFlow.first()
                val workRequest = PeriodicWorkRequestBuilder<ReminderWorker>(
                    intervalMins.toLong(),
                    TimeUnit.MINUTES
                ).build()

                workManager.enqueueUniquePeriodicWork(
                    "naam_jap_periodic_work",
                    ExistingPeriodicWorkPolicy.UPDATE,
                    workRequest
                )
            }
        }
    }
}
