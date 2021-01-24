package com.example.lockerbox.Services

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build

class App : Application() {
    companion object {
        const val CHANNEL_ID : String = "LokerChanel"
        var time_in_milli_seconds = 0L
        var lastTime = 0L
        var afterAddTime = 0L
        var id = 0
        var codelocker = ""
        var noBox = 0
        var password = ""
    }


    override fun onCreate() {
        super.onCreate()

        CreateNotification()
    }

    private fun CreateNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val name = "Loker Channel Service"
            val descriptiontxt = "Notification Text"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptiontxt
            }
            val notificationManager: NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}