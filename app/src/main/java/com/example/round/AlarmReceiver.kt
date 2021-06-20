package com.example.round

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.core.app.NotificationCompat
import java.time.LocalDateTime

class AlarmReceiver : BroadcastReceiver() {

    companion object {
        const val TAG = "AlarmReceiver"
        const val PRIMARY_CHANNEL_ID = "primary_notification_channel"
    }

    lateinit var notificationManager: NotificationManager
    lateinit var DBHelper : rDBHelper
    override fun onReceive(context: Context, intent: Intent) {
        DBHelper = rDBHelper(context)
        val code = intent.getIntExtra("code", -1)
        val rid = intent.getIntExtra("rid", -1)
        var isEnd = intent.getBooleanExtra("isEnd", false) //마지막 스케줄인지 확인

        notificationManager = context.getSystemService(
            Context.NOTIFICATION_SERVICE) as NotificationManager

        createNotificationChannel()
        deliverNotification(context, code, rid, isEnd)
    }

    private fun deliverNotification(context: Context, code:Int, rid:Int, isEnd:Boolean) {
        val contentIntent = Intent(context, MainActivity::class.java)
        contentIntent.putExtra("time", code)
        contentIntent.putExtra("rid", rid)
        contentIntent.putExtra("isEnd", isEnd)

        val contentPendingIntent = PendingIntent.getActivity(
            context,
            code,
            contentIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        val builder =
            NotificationCompat.Builder(context, PRIMARY_CHANNEL_ID)
                .setSmallIcon(R.drawable.memo)
                .setContentTitle("Alert")
                .setContentText("조삼..모사..")
                .setContentIntent(contentPendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL)

        notificationManager.notify(code, builder.build())
    }

    fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                PRIMARY_CHANNEL_ID,
                "Stand up notification",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableVibration(true)
            notificationChannel.description = "AlarmManager Tests"
            notificationManager.createNotificationChannel(
                notificationChannel)
        }
    }
}