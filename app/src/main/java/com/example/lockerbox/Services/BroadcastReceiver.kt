package com.example.lockerbox.Services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.lockerbox.HomeFragment
import com.example.lockerbox.MainActivity

class BroadcastReceiver: BroadcastReceiver() {

//    var myService: LockerService? = null

    override fun onReceive(p0: Context?, p1: Intent?) {
        LockerService.isaddTime = true
        LockerService.isRunning = false
        var time = 1
        LockerService.notificationManager.cancel(2)
        LockerService.countDownTimer.cancel()
//        App.time_in_milli_seconds = App.lastTime + time*30000L
        App.afterAddTime = App.lastTime + time*900000L
        Log.d("p1", LockerService.isaddTime.toString())
        Log.d("p1", "Last Time "+ App.lastTime.toInt().toString())
        Log.d("p1", "Time after add " + App.time_in_milli_seconds.toString())
        Log.d("p1", "Time after add " + App.afterAddTime.toString())

        stopService(p0!!)
        startService(p0!!)
    }

    fun startService(context: Context){
        HomeFragment.serviceIntent = Intent(context, LockerService::class.java)
        HomeFragment.serviceIntent.putExtra("EXTRA_TIME", App.afterAddTime.toString())
        HomeFragment.serviceIntent.putExtra("EXTRA_ID", App.noBox.toString())
        HomeFragment.serviceIntent.setAction(LockerService.ACTION_START_FOREGROUND_SERVICE)
        context.startService(HomeFragment.serviceIntent)
        Log.d("p2", "Start Service")
    }

    fun stopService(context: Context){
        HomeFragment.serviceIntent = Intent(context, LockerService::class.java)
        context.stopService(HomeFragment.serviceIntent)
    }

}