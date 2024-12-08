package com.example.nichenhl

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import android.util.Log

private const val TAG = "NotificationReceiver"

class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d(TAG, "NotificationReceiver triggered!")

        val homeTeam = intent?.getStringExtra("homeTeam") ?: "Unknown Home Team"
        val awayTeam = intent?.getStringExtra("awayTeam") ?: "Unknown Away Team"

        if (context != null) {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            // Create notification channel for API >= 26
            val channelId = "GAME_NOTIFICATION_CHANNEL"
            val channelName = "Game Notifications"
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                val channel = NotificationChannel(
                    channelId,
                    channelName,
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = "Notifications for game start reminders."
                }
                notificationManager.createNotificationChannel(channel)
            }

            // Build the notification
            val notification = NotificationCompat.Builder(context, channelId)
                .setContentTitle("Game Reminder")
                .setContentText("$awayTeam @ $homeTeam starts in 15 minutes!")
                .setSmallIcon(R.drawable.baseline_sports_hockey_24)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .build()

            // Show the notification
            notificationManager.notify(0, notification)
            Log.d(TAG, "Notification sent: $homeTeam vs $awayTeam")
        }
    }
}
