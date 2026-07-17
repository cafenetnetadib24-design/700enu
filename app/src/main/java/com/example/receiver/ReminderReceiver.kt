package com.example.receiver

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.core.app.NotificationCompat
import com.example.MainActivity

class ReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        showNotificationWithVibration(context)
    }

    private fun showNotificationWithVibration(context: Context) {
        val channelId = "daily_study_reminder_channel"
        val notificationId = 1001

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Create Notification Channel for Android Oreo+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "یادآور روزانه مطالعه",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "اعلان روزانه برای تکمیل آموزش‌ها"
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 500, 200, 500)
            }
            notificationManager.createNotificationChannel(channel)
        }

        // Action when clicking the notification (opens MainActivity)
        val openAppIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            openAppIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Build notification
        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("یادآوری مطالعه روزانه 📚")
            .setContentText("باید آموزش‌های امروز را تکمیل کنی")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setVibrate(longArrayOf(0, 500, 200, 500))

        notificationManager.notify(notificationId, builder.build())

        // Trigger manual phone vibration
        try {
            val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(500)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
