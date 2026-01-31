package com.example.cpreminder

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

class DailyAlarmWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        showDeepNotification()
        return Result.success()
    }

    private fun showDeepNotification() {
        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "cp_alarm_channel"

        val channel = NotificationChannel(channelId, "CP Daily Alarm", NotificationManager.IMPORTANCE_HIGH).apply {
            enableVibration(true)
        }
        notificationManager.createNotificationChannel(channel)

        val notification = NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setContentTitle("OI! SUBMIT NOW!")
            .setContentText("Your streak is in danger. Go solve one problem on CF!")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setVibrate(longArrayOf(0, 500, 200, 500))
            .build()

        notificationManager.notify(101, notification)
    }
}