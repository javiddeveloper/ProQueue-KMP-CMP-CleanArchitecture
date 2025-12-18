package xyz.sattar.javid.proqueue.core.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat

class ReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val customerName = intent.getStringExtra("customerName") ?: ""
        val businessName = intent.getStringExtra("businessName") ?: ""
        val minutesBefore = intent.getIntExtra("minutesBefore", 10)
        
        showNotification(context, customerName, businessName, minutesBefore)
    }

    private fun showNotification(context: Context, customerName: String, businessName: String, minutesBefore: Int) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "appointment_reminders"
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Appointment Reminders",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Reminders for upcoming appointments"
            }
            notificationManager.createNotificationChannel(channel)
        }

        // Using system default icon if specific resource is not found to avoid compilation errors
        // In a real app, use R.drawable.ic_notification or similar
        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info) 
            .setContentTitle("یادآوری نوبت")
            .setContentText("نوبت $customerName در کسب و کار $businessName تا $minutesBefore دقیقه دیگر نزدیک هست برای اطلاع از نوبت اینجا را لمس کنید")
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText("نوبت $customerName در کسب و کار $businessName تا $minutesBefore دقیقه دیگر نزدیک هست برای اطلاع از نوبت اینجا را لمس کنید"))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }
}
