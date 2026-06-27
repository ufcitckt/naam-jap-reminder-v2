package com.naamjap.reminder

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Build
import android.speech.tts.TextToSpeech
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.delay
import java.util.Calendar
import java.util.Locale

class ReminderWorker(
    private val context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val dataStoreManager = DataStoreManager(context)
        val database = MalaDatabase.getDatabase(context)
        val dao = database.chantLogDao()

        val showNotification = dataStoreManager.showNotificationFlow.first()
        val playSound = dataStoreManager.playSoundFlow.first()
        val soundType = dataStoreManager.soundTypeFlow.first()
        val quietHoursEnabled = dataStoreManager.quietHoursEnabledFlow.first()
        val quietHoursStart = dataStoreManager.quietHoursStartFlow.first()
        val quietHoursEnd = dataStoreManager.quietHoursEndFlow.first()

        val inQuietHours = quietHoursEnabled && isInQuietHours(quietHoursStart, quietHoursEnd)

        if (inQuietHours) {
            dao.insertLog(ChantLog(
                timestamp = System.currentTimeMillis(),
                type = "REMINDER",
                detail = "Silent (Quiet Hours Boundary)"
            ))
            return Result.success()
        }

        if (showNotification) {
            sendNotification()
        }

        if (playSound) {
            when (soundType) {
                "tts" -> {
                    speakMantra()
                }
                "bell" -> {
                    playTone(ToneGenerator.TONE_CDMA_PIP)
                }
                else -> {
                    playTone(ToneGenerator.TONE_PROP_BEEP)
                }
            }
        }

        dao.insertLog(ChantLog(
            timestamp = System.currentTimeMillis(),
            type = "REMINDER",
            detail = "Alert Triggered (${soundType.uppercase(Locale.ROOT)})"
        ))

        return Result.success()
    }

    private fun isInQuietHours(start: String, end: String): Boolean {
        try {
            val calendar = Calendar.getInstance()
            val currentHour = calendar.get(Calendar.HOUR_OF_DAY)
            val currentMinute = calendar.get(Calendar.MINUTE)
            val currentMins = currentHour * 60 + currentMinute

            val startParts = start.split(":")
            val startMins = startParts[0].toInt() * 60 + startParts[1].toInt()

            val endParts = end.split(":")
            val endMins = endParts[0].toInt() * 60 + endParts[1].toInt()

            return if (startMins < endMins) {
                currentMins in startMins..endMins
            } else {
                currentMins >= startMins || currentMins <= endMins
            }
        } catch (e: Exception) {
            return false
        }
    }

    private fun sendNotification() {
        val channelId = "naam_jap_channel"
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Naam Jap Reminder",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Reminders for chanting Naam Jap"
                enableVibration(true)
            }
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setContentTitle("🌸 Naam Jap Reminder")
            .setContentText("राधा वल्लभ श्री हरिवंश | Radha Vallabh Shri Harivansh")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }

    private fun playTone(toneType: Int) {
        try {
            val toneGenerator = ToneGenerator(AudioManager.STREAM_NOTIFICATION, 100)
            toneGenerator.startTone(toneType, 400)
            toneGenerator.release()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private suspend fun speakMantra() {
        var tts: TextToSpeech? = null
        try {
            tts = TextToSpeech(context) { status ->
                if (status == TextToSpeech.SUCCESS) {
                    tts?.language = Locale.US
                    tts?.speak("Radha Vallabh Shri Harivansh", TextToSpeech.QUEUE_FLUSH, null, null)
                }
            }
            delay(3000)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            tts?.shutdown()
        }
    }
}
