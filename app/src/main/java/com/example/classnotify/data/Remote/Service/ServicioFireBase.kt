package com.example.classnotify.data.Remote.Service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.example.classnotify.MainActivity
import com.example.classnotify.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlin.random.Random

class ServicioFireBase : FirebaseMessagingService(){
    private val random = Random

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onMessageReceived(message: RemoteMessage) {
        message.notification?.let { message ->
            Log.i("FCM Title", "${message.title}")
            Log.i("FCM Body", "${message.body}")
            sendNotification(message)
        }
    }
        @RequiresApi(Build.VERSION_CODES.O)
        fun  sendNotification (notification: RemoteMessage.Notification) {
            val intent = Intent(this, MainActivity::class.java).apply{
                addFlags(FLAG_ACTIVITY_CLEAR_TOP)
        }
        val pendingIntent = PendingIntent.getActivity(
            this, 0,intent, FLAG_IMMUTABLE
        )
            val channelId = this.getString(R.string.default_notificacion_channel_id)
            val notificationBuilder = NotificationCompat.Builder(this, channelId)
                .setContentTitle(notification.title)
                .setContentText(notification.body)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setSmallIcon(R.drawable.vector)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)

            val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                val channel = NotificationChannel(channelId, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH)
                manager.createNotificationChannel(channel)
            }
            manager.notify(random.nextInt(), notificationBuilder.build())
    }


    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }
    companion object {
        const val CHANNEL_NAME = "FCM Notification channel"
    }
}
