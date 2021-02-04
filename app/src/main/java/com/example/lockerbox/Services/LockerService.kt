package com.example.lockerbox.Services

import android.app.AlertDialog
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.CountDownTimer
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.lockerbox.HomeFragment
import com.example.lockerbox.MainActivity
import com.example.lockerbox.R
import com.example.lockerbox.Room.*
import com.example.lockerbox.api.ResponseAPI
import com.example.lockerbox.api.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LockerService: Service() {

    private val mBinder: IBinder = MyBinder()
    lateinit var dao : LockerBoxDao

    companion object {

        lateinit var countDownTimer: CountDownTimer
        lateinit var notificationManager: NotificationManager
        lateinit var notification : NotificationCompat.Builder
        lateinit var mLockerBoxViewModel : LockerBoxViewModel

        var end_time : Long = 0L
        var isRunning: Boolean = false
        var isaddTime: Boolean = false
        var notifadd15: Boolean = false
        var id_notification: Int = 0


        val ACTION_START_FOREGROUND_SERVICE = "ACTION_START_FOREGROUND_SERVICE"

        val ACTION_STOP_FOREGROUND_SERVICE = "ACTION_STOP_FOREGROUND_SERVICE"

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("p2", "id $id_notification")
        if (intent != null) {
            val action = intent.action
            when (action) {
                ACTION_START_FOREGROUND_SERVICE -> {
                    Log.d("p2", "id start one $id_notification")
                    val time: String? = intent?.getStringExtra("EXTRA_TIME")
                    id_notification = intent?.getStringExtra("EXTRA_ID")?.toInt() ?:
                            Log.d("p1", "Time in milisecond " + App.time_in_milli_seconds.toString())
                    Log.d("p1", "Is Running " + isRunning.toString())
                    Log.d("p1", "Is Add time " + isaddTime.toString())
                    if(!isaddTime){
            App.time_in_milli_seconds = time!!.toLong() *20000L
//            App.time_in_milli_seconds = time!!.toLong() *3600000L
//                        App.time_in_milli_seconds = time!!.toLong() *960000L
                    }
                    else{
                        App.time_in_milli_seconds = time!!.toLong()
                    }

                    notificationForeground()
                    if (!isRunning){
                        StartTimer(App.time_in_milli_seconds)
                    }
                }
                ACTION_STOP_FOREGROUND_SERVICE -> {
                    Log.d("p2", "Stop Servicessssss")
                    Log.d("p2", "Stop foreground service.")
                    FinishRentHome()
                }
            }
        }

        return START_NOT_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onBind(p0: Intent?): IBinder? {
        return mBinder
    }

    inner class MyBinder : Binder() {
        fun getService(): LockerService{
            return this@LockerService
        }
    }

    fun StartTimer(times: Long) {
        end_time = System.currentTimeMillis() + times
        countDownTimer = object : CountDownTimer(times, 1000){
            override fun onTick(p0: Long) {
                App.time_in_milli_seconds = p0
                App.lastTime = p0

                val (H, M, S) = TimeUpdateText()

                notification.setContentText("Sisa Waktu $H:$M:$S")
                notificationManager.notify(id_notification, notification.build())
            }

            override fun onFinish() {
                FinishRent()
            }
        }
        countDownTimer.start()
        isRunning = true
        StartForegroundnotif(foregroundvar = true)
    }

    fun FinishRent(){
        isRunning = false
        isaddTime = false
        notifadd15 = false
        StartForegroundnotif(foregroundvar = false)
        notificationtimeend()
        updateLockerBox()
        timeOutRentBox()
    }

    private fun timeOutRentBox(){
        RetrofitClient.instance.patchBoxLocker(
                App.id,
                null,
                null,
                null,
                null,
                null,
                null,
                true
        ).enqueue(object : Callback<ResponseAPI> {
            override fun onResponse(call: Call<ResponseAPI>, response: Response<ResponseAPI>) {
                Log.d("p3", "response up " + response.body().toString())
            }

            override fun onFailure(call: Call<ResponseAPI>, t: Throwable) {
                Log.d("p3", "fail up " + t.message.toString())
            }
        })
    }

    fun FinishRentHome(){
        isRunning = false
        isaddTime = false
        notifadd15 = false
        countDownTimer.cancel()
        notificationManager.cancel(id_notification)
        Log.d("p2", "id stop $id_notification")
        stopForeground(true)
        stopSelf()
    }

    fun updateLockerBox(){
        val updatedLockerBox = LockerBox(App.id, App.codelocker, App.noBox, App.password, duration = 0, TimeOut = true)
        Log.d("p2", "Sukses.....")
        Thread{
            dao = LBDatabase.getDatabase(application).LockerBoxDao()
            dao.updateLockerBox(updatedLockerBox)
            Log.d("p2", "Sukses")
        }.start()
//        repository.updateLockerBox(updatedLockerBox)
//        mLockerBoxViewModel.deleteLockerBox(updatedLockerBox)
    }

    fun notificationForeground() {
        Log.d("p1", "Time on Service " + App.time_in_milli_seconds.toString())
        Log.d("p2", "id start two $id_notification")

        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationIntent = Intent(this, MainActivity::class.java)
        val PendingIntent = PendingIntent.getActivity(this, id_notification, notificationIntent, id_notification)
        notification = NotificationCompat.Builder(this, App.CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_dialogicon)
                .setVibrate(null)
                .setOnlyAlertOnce(true)
                .setOngoing(true)
                .setContentTitle("Peminjaman Loker nomor $id_notification Dalam Waktu ${App.time_in_milli_seconds / 3600000} Jam")
                .setContentIntent(PendingIntent)
    }

    fun StartForegroundnotif(foregroundvar: Boolean){
        if(foregroundvar){
            startForeground(id_notification, notification.build())
        }
        else{
            stopForeground(true)
            stopSelf()
        }
    }

    fun TimeUpdateText(): Triple<Int, Int, Int>{
        val Hour = (App.time_in_milli_seconds/3600000).toInt()
        val Minute = ((App.time_in_milli_seconds/60000)%60).toInt()
        val Second = ((App.time_in_milli_seconds/1000)%60).toInt()

        val intentVar = Intent(this, BroadcastReceiver::class.java)
        val IntentBroadcast = PendingIntent.getBroadcast(applicationContext, 100, intentVar, 0)


        if ((Minute == 14 && Hour == 0) && !isaddTime && !notifadd15){
            notificationtime15()
            notifadd15 = true
            notification.addAction(
                    R.drawable.ic_dialogicon,
                    "Tambah Waktu 15 Menit",
                    IntentBroadcast
            )
            notificationManager.notify(id_notification, notification.build())
        }
        else if (isaddTime){
            notification.mActions.clear()
            notification.setContentTitle("Peminjaman Loker nomor $id_notification Tambahan Waktu 15 Menit")
            notificationManager.notify(id_notification, notification.build())
            notificationManager.cancel(id_notification + 1)
        }

        return Triple(Hour, Minute, Second)
    }

    fun notificationtime15(){
        val builder = NotificationCompat.Builder(this, App.CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_dialogicon)
                .setContentTitle("Peringatan Waktu Peminjaman")
                .setContentText("Waktu Peminjaman anda kurang 15 menit lagi")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        with(NotificationManagerCompat.from(this)){
            notify(id_notification + 1, builder.build())
        }
    }

    fun notificationtimeend(){
        val builder = NotificationCompat.Builder(this, App.CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_dialogicon)
                .setContentTitle("Waktu Peminjaman Habis")
                .setStyle(NotificationCompat.BigTextStyle().bigText("Waktu Peminjaman anda Habis, Silahkan hubungi admin Loker anda meminjam untuk mengambil barang anda"))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        with(NotificationManagerCompat.from(this)){
            notify(id_notification + 1, builder.build())
        }
    }
}