package com.example.lockerbox

import android.Manifest
import android.app.AlertDialog
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.lockerbox.Adapter.HomeAdapter
import com.example.lockerbox.Room.LockerBox
import com.example.lockerbox.Room.LockerBoxViewModel
import com.example.lockerbox.Services.LockerService
import com.example.lockerbox.api.ResponseAPI
import com.example.lockerbox.api.RetrofitClient
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import kotlinx.android.synthetic.main.custom_dialog.*
import kotlinx.android.synthetic.main.custom_dialog.view.*
import kotlinx.android.synthetic.main.custom_dialog.view.NumberBox
import kotlinx.android.synthetic.main.custom_dialog_home.*
import kotlinx.android.synthetic.main.custom_dialog_home.view.*
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_home.view.*
import kotlinx.android.synthetic.main.fragment_response_locker.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeFragment : Fragment(), View.OnClickListener {

//    private lateinit var mLockerBoxViewModel : LockerBoxViewModel
    private val listBox = emptyList<LockerBox>()
    private val list = ArrayList<ResponseAPI>()
    private var wrongPassword = 3

    private var isRunning: Boolean = false
    private var time_in_milli_seconds = 0L
    private var end_time : Long = 0L
    private val Start_Time_In_Millis = 60000L

    private lateinit var countDownTimer: CountDownTimer
    private lateinit var prefs: SharedPreferences

//    lateinit var serv : LockerService

    companion object{
//        lateinit var mLockerBoxViewModel : LockerBoxViewModel
        lateinit var serviceIntent: Intent

//        var button_stat : Boolean = false
        var button_open : Boolean = false
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapterHome()

        val BtnScan : Button = view.findViewById(R.id.ButtonScan)
        BtnScan.setOnClickListener(this)
    }

    private fun adapterHome(){

//        val rvBox = view?.rvView
        rvView.setHasFixedSize(true)
        rvView.layoutManager = GridLayoutManager(requireContext(), 2)

//        rvView.layoutManager = LinearLayoutManager(requireContext())
        val adapter = HomeAdapter(listBox)
        rvView.adapter = adapter

        LockerService.mLockerBoxViewModel = ViewModelProvider(this).get(LockerBoxViewModel::class.java)
        LockerService.mLockerBoxViewModel.readAllData.observe(viewLifecycleOwner, Observer {
            box -> adapter.setData(box)
            Log.d("CREATION",adapter.itemCount.toString())
            if (adapter.itemCount != 0) {
                Log.d("CREATION","Show False")
                showTextEmpty(false)
            } else {
                Log.d("CREATION","Show True")
                showTextEmpty(true)
            }
        })

        adapter.setOnItemClickCallback(object : HomeAdapter.OnItemClickCallback {
            override fun onItemClicked(data: LockerBox) {
                showCustomDialog(data)
            }

        })
    }

    override fun onClick(p0: View?) {
        when (p0!!.id) {
            R.id.ButtonScan -> {
//                Toast.makeText(context, "You clicked me.", Toast.LENGTH_SHORT).show()

                val permissionlistener: PermissionListener = object : PermissionListener {
                    override fun onPermissionGranted() {
                        showScanFragment()
                    }

                    override fun onPermissionDenied(deniedPermissions: List<String>) {
                        Toast.makeText(
                            context,
                            "Permission Denied\n$deniedPermissions",
                            Toast.LENGTH_SHORT
                        ).show()
                        findNavController().navigate(R.id.action_homeFragment_to_scanFragment)
                    }
                }

                TedPermission.with(context)
                    .setPermissionListener(permissionlistener)
                    .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                    .setPermissions(Manifest.permission.CAMERA)
                    .check()
            }
        }
    }

    private fun showScanFragment(){
        findNavController().navigate(R.id.action_homeFragment_to_scanFragment)
    }

    private fun showTextEmpty(state: Boolean) {
        if (state) {
            imageEmpty.visibility = View.VISIBLE
            EmptyText.visibility = View.VISIBLE
            ButtonScan.visibility = View.VISIBLE
        } else {
            imageEmpty.visibility = View.GONE
            EmptyText.visibility = View.GONE
            ButtonScan.visibility = View.GONE
        }
    }

    private fun showCustomDialog(data: LockerBox) {
        val mDialogView = LayoutInflater.from(requireContext()).inflate(R.layout.custom_dialog_home, null)
        val mBuilder = AlertDialog.Builder(requireContext())
            .setView(mDialogView)
            .setTitle("Informasi Box")

        button_open = data.TimeOut!!
        mDialogView.NumberBox.setText(data.noBox.toString())
        mDialogView.InputPasswordHome.setText(data.password)
        mDialogView.InputDurationHome.setText(data.duration.toString() + " Jam")
        ButtonsUpdate(mDialogView)
        onStartState(mDialogView)
        Log.d("CREATION", "TimeOut  " + data.TimeOut.toString())

        val mAlertDialog = mBuilder.show()

        mDialogView.InputPasswordHome.setOnFocusChangeListener { view, b ->
            mDialogView.InputPasswordHome.setText("")
        }

        mDialogView.ButtonOpen.setOnClickListener {
            val password = mAlertDialog.InputPasswordHome.text.toString()

//            Toast.makeText(requireContext(), "Kamu sukses memasukkan " + password + " dan " + duration , Toast.LENGTH_SHORT).show()

            RetrofitClient.instance.getIdPassword(data.idBox,password).enqueue(object : Callback<List<ResponseAPI>> {
                override fun onResponse(call: Call<List<ResponseAPI>>, response: Response<List<ResponseAPI>>) {
                    list.clear()
                    response.body()?.let { list.addAll(it) }
                    if (list.size == 0){
                        wrongPassword -= 1
                        Toast.makeText(requireContext(), "Password Salah, Sisa Kesempatan $wrongPassword" , Toast.LENGTH_SHORT).show()
                        if(wrongPassword == 0){
                            if (!isRunning){
                                wrongPasswordTimer(Start_Time_In_Millis,mDialogView)
                            }
                        }
                    }
                    else{
                        openDoorBox(data, mAlertDialog)
//                        mAlertDialog.dismiss()
                    }

                }

                override fun onFailure(call: Call<List<ResponseAPI>>, t: Throwable) {
                    Log.d("p2", t.message.toString())
                    if (t.message == t.message){
                        Toast.makeText(requireContext(), "Tidak ada koneksi Internet" , Toast.LENGTH_SHORT).show()
                    }
                }

            })
        }

        mDialogView.ButtonEnd.setOnClickListener {
            finishRentBox(data, mAlertDialog)
        }
    }

    fun FinishRent(){
        LockerService.isRunning = false
        LockerService.isaddTime = false
        LockerService.notifadd15 = false

        LockerService.countDownTimer.cancel()

        serviceIntent = Intent(activity, LockerService::class.java)
        requireActivity().stopService(serviceIntent)
    }

    private fun wrongPasswordTimer(times: Long, mDialogView: View){
        end_time = System.currentTimeMillis() + times
        countDownTimer = object : CountDownTimer(times, 1000){
            override fun onTick(p0: Long) {
                time_in_milli_seconds = p0
                UpdateTextCount(mDialogView)
            }

            override fun onFinish() {
                isRunning = false
                wrongPassword = 3
                ButtonsUpdate(mDialogView)
            }
        }.start()
        isRunning = true
        ButtonsUpdate(mDialogView)
    }

    private fun ButtonsUpdate(mDialogView: View){
        if(isRunning){
            mDialogView.ButtonOpen.visibility = View.INVISIBLE
            mDialogView.textTimerWrongPassword.visibility = View.VISIBLE
        }
        else if(button_open){
            mDialogView.ButtonOpen.visibility = View.INVISIBLE
            mDialogView.textTimerWrongPassword.visibility = View.INVISIBLE
        }
        else{
            mDialogView.ButtonOpen.visibility = View.VISIBLE
            mDialogView.textTimerWrongPassword.visibility = View.INVISIBLE
        }
    }

    private fun UpdateTextCount(mDialogView: View) {
        val second = ((time_in_milli_seconds/1000)%60).toInt()

        mDialogView.textTimerWrongPassword.text = "Tunggu $second detik untuk memasukkan password lagi"
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putLong("millisLeft", time_in_milli_seconds)
        outState.putBoolean("timerisrunning", isRunning)
        outState.putLong("EndTimes", end_time)
        outState.putBoolean("ButtonStat", button_open)
    }

    override fun onStop() {
        super.onStop()
        prefs = requireActivity().getPreferences(MODE_PRIVATE)
        val editor : SharedPreferences.Editor = prefs.edit()

        editor.putLong("millisLeft", time_in_milli_seconds)
        editor.putBoolean("timerisrunning", isRunning)
        editor.putLong("EndTimes", end_time)
        editor.putBoolean("ButtonStat", button_open)

        editor.apply()

    }

    override fun onStart() {
        super.onStart()
        prefs = requireActivity().getPreferences(MODE_PRIVATE)
        time_in_milli_seconds = prefs.getLong("millisLeft",Start_Time_In_Millis)
        isRunning = prefs.getBoolean("timerisrunning",false)
        end_time = prefs.getLong("EndTimes",0)
        button_open = prefs.getBoolean("ButtonStat",false)
    }

    fun onStartState(mDialogView: View) {
        button_open = prefs.getBoolean("ButtonStat",false)
        if (isRunning){
            time_in_milli_seconds = end_time - System.currentTimeMillis()
            if(time_in_milli_seconds < 0){
                time_in_milli_seconds = 0
                isRunning = false
                UpdateTextCount(mDialogView)
                ButtonsUpdate(mDialogView)
            }
            else{
                wrongPasswordTimer(time_in_milli_seconds,mDialogView)
            }
        }
    }

    private fun openDoorBox(data: LockerBox, mAlertDialog: AlertDialog){
        RetrofitClient.instance.patchBoxLocker(
                data.idBox!!,
                null,
                null,
                null,
                null,
                null,
                true,
                null
        ).enqueue(object : Callback<ResponseAPI>{
            override fun onResponse(call: Call<ResponseAPI>, response: Response<ResponseAPI>) {
                Toast.makeText(requireContext(), "Kamu berhasil membuka pintu nomor ${data.noBox}" , Toast.LENGTH_SHORT).show()
                wrongPassword = 3
                mAlertDialog.dismiss()
            }

            override fun onFailure(call: Call<ResponseAPI>, t: Throwable) {
                Toast.makeText(requireContext(), "Kamu gagal membuka pintu nomor ${data.noBox}" , Toast.LENGTH_SHORT).show()
                Log.d("p2", t.message.toString())
                if (t.message == t.message){
                    Toast.makeText(requireContext(), "Tidak ada koneksi Internet" , Toast.LENGTH_SHORT).show()
                }
            }

        })
    }


    private fun finishRentBox(data: LockerBox, mAlertDialog: AlertDialog){
        RetrofitClient.instance.putBoxLocker(
                data.idBox!!,
                data.codeLocker,
                data.noBox,
                "", //nanti di clear password jangan lupa!!
                0,
                false,
                false,
                false
        ).enqueue(object : Callback<ResponseAPI>{
            override fun onResponse(call: Call<ResponseAPI>, response: Response<ResponseAPI>) {
                Log.d("p2", response.body().toString())
                if(LockerService.isRunning){
                    Log.d("p2", button_open.toString())
                    FinishRent()
                }
                deleteBox(data)
                mAlertDialog.dismiss()
            }

            override fun onFailure(call: Call<ResponseAPI>, t: Throwable) {
                Log.d("p2", t.message.toString())
                if (t.message == t.message){
                    Toast.makeText(requireContext(), "Tidak ada koneksi Internet" , Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    private fun deleteBox(data: LockerBox){
        LockerService.mLockerBoxViewModel.deleteLockerBox(data)
//        Toast.makeText(requireContext(), "Kamu sukses menghapus data dari DB", Toast.LENGTH_LONG).show()
    }

}