package com.harvestpay.app.notification

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.harvestpay.app.MainActivity
import com.harvestpay.app.R
import java.util.concurrent.TimeUnit

class ReminderWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        createChannel(applicationContext)
        if (Build.VERSION.SDK_INT >= 33 && ActivityCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.POST_NOTIFICATIONS,
            ) != PackageManager.PERMISSION_GRANTED
        ) return Result.success()

        val reminderId = inputData.getLong(KEY_ID, 0L)
        val customer = inputData.getString(KEY_CUSTOMER).orEmpty()
        val message = inputData.getString(KEY_MESSAGE).orEmpty()
        val openIntent = Intent(applicationContext, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            applicationContext,
            reminderId.toInt(),
            openIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher)
            .setContentTitle("Payment reminder: $customer")
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()
        NotificationManagerCompat.from(applicationContext).notify(reminderId.toInt(), notification)
        return Result.success()
    }

    companion object {
        private const val CHANNEL_ID = "payment_reminders"
        private const val KEY_ID = "id"
        private const val KEY_CUSTOMER = "customer"
        private const val KEY_MESSAGE = "message"

        fun schedule(context: Context, id: Long, customer: String, message: String, triggerAt: Long) {
            val delay = (triggerAt - System.currentTimeMillis()).coerceAtLeast(0L)
            val data = Data.Builder()
                .putLong(KEY_ID, id)
                .putString(KEY_CUSTOMER, customer)
                .putString(KEY_MESSAGE, message)
                .build()
            val request = OneTimeWorkRequestBuilder<ReminderWorker>()
                .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                .setInputData(data)
                .build()
            WorkManager.getInstance(context).enqueueUniqueWork(
                "harvest-reminder-$id",
                androidx.work.ExistingWorkPolicy.REPLACE,
                request,
            )
        }

        fun cancel(context: Context, id: Long) {
            WorkManager.getInstance(context).cancelUniqueWork("harvest-reminder-$id")
        }

        private fun createChannel(context: Context) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(
                    CHANNEL_ID,
                    "Payment reminders",
                    NotificationManager.IMPORTANCE_HIGH,
                ).apply { description = "Reminders for pending Harvest Pay balances" }
                context.getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
            }
        }
    }
}
