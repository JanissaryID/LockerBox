package com.example.lockerbox

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import com.example.lockerbox.Adapter.LockerAdapter
import com.example.lockerbox.Room.LockerBox
import com.example.lockerbox.Room.LockerBoxViewModel
import com.example.lockerbox.Services.App
import com.example.lockerbox.Services.LockerService
import com.example.lockerbox.api.ResponseAPI
import com.example.lockerbox.api.RetrofitClient
import kotlinx.android.synthetic.main.custom_dialog.*
import kotlinx.android.synthetic.main.custom_dialog.view.*
import kotlinx.android.synthetic.main.fragment_response_locker.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class ResponseLockerFragment : Fragment() {

    private val args : ResponseLockerFragmentArgs by navArgs()
    private val list = ArrayList<ResponseAPI>()
    private val listcheck = ArrayList<ResponseAPI>()

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_response_locker, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        HomeFragment.mLockerBoxViewModel = ViewModelProvider(this).get(LockerBoxViewModel::class.java)

        getdata()
    }

    private fun getdata()
    {
        rvBoxPost.setHasFixedSize(true)
        rvBoxPost.layoutManager = GridLayoutManager(requireContext(), 2)

        RetrofitClient.instance.getBoxLocker(args.CodeLocker).enqueue(object : Callback<List<ResponseAPI>> {
            override fun onResponse(call: Call<List<ResponseAPI>>, response: Response<List<ResponseAPI>>) {
//                val responseCode = response.code().toString() + "   " + args.CodeLocker
//                tvResponseCode.text = responseCode
                tvResponseCode.text = "Loker ${args.CodeLocker}"
                response.body()?.let { list.addAll(it) }

                val adapter = LockerAdapter(list)
                rvBoxPost.adapter = adapter

                showLoading(true)
                if (list != null) {
                    showLoading(false)
                } else {
                    showLoading(true)
                }

                adapter.setOnItemClickCallback(object : LockerAdapter.OnItemClickCallback {
                    override fun onItemClicked(data: ResponseAPI) {
                        showCustomDialog(data)
                    }

                })

            }

            override fun onFailure(call: Call<List<ResponseAPI>>, t: Throwable) {
                Log.d("p2", t.message.toString())
                if (t.message == t.message){
                    Toast.makeText(requireContext(), "Tidak ada koneksi Internet" , Toast.LENGTH_SHORT).show()
                }
            }

        })
    }

    private fun showCustomDialog(data: ResponseAPI) {
        var showerror : Boolean = true
        val mDialogView = LayoutInflater.from(requireContext()).inflate(R.layout.custom_dialog, null)
        val mBuilder = AlertDialog.Builder(requireContext())
                .setView(mDialogView)
                .setTitle("LockerBox")

        mDialogView.NumberBox.setText(data.noBox.toString())

        var HoursList = arrayOf("1 Jam", "2 Jam", "3 Jam", "4 Jam")

        var adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, HoursList)
        mDialogView.autoCompleteText.threshold = 0
        mDialogView.autoCompleteText.setAdapter(adapter)
        mDialogView.autoCompleteText.setOnFocusChangeListener { view, b -> if(b) mDialogView.autoCompleteText.showDropDown()}

        val mAlertDialog = mBuilder.show()

        mDialogView.ButtonRent.setOnClickListener {

            val password = mAlertDialog.InputPassword.text.toString().trim()
            val duration = mAlertDialog.autoCompleteText.text.toString().trim()

            if(password.isNullOrEmpty()){
                mDialogView.InputPassword.error = "Tidak boleh kosong \nHarus lebih dari 8 karakter"
                showerror = false
            }
            else if(password.length < 8){
                mDialogView.InputPassword.error = "Harus lebih dari 8 karakter"
                showerror = false
            }
            else if(duration.isNullOrEmpty()){
                mDialogView.autoCompleteText.error = "Tidak boleh Kosong"
                showerror = false
            }
            else{
                showerror = true
            }

            if(showerror){
                checkBox(data.id!!, data, password, duration.subSequence(0, 1).toString().toInt(), mAlertDialog)
//                updateBox(data, password, duration.subSequence(0, 1).toString().toInt(), mAlertDialog)
            }
        }
    }

    fun startService(times: Int, nobox: Int){
        HomeFragment.serviceIntent = Intent(activity, LockerService::class.java)
        HomeFragment.serviceIntent.putExtra("EXTRA_TIME", times.toString())
        HomeFragment.serviceIntent.putExtra("EXTRA_ID", nobox.toString())
        App.noBox = nobox
        HomeFragment.serviceIntent.setAction(LockerService.ACTION_START_FOREGROUND_SERVICE)
        requireActivity().startService(HomeFragment.serviceIntent)
//        Toast.makeText(requireContext(), "Service Start", Toast.LENGTH_SHORT).show()
    }

    private fun insertDataToDB(data: ResponseAPI, datapassword: String, dataduration: Int) {
        val id = data.id
        val codelocker = data.codeLocker
        val nobox = data.noBox
        val password = datapassword
        val duration = dataduration

        App.id = data.id!!
        App.codelocker = data.codeLocker.toString()
        App.password = password

        if(inputCheck(password, duration.toString())){
            val lockerBox = LockerBox(id, codelocker, nobox, password, duration, false)
            LockerService.mLockerBoxViewModel.addLockerBox(lockerBox)
//            Toast.makeText(requireContext(), "Kamu sukses memasukkan data ke DB", Toast.LENGTH_LONG).show()
            findNavController().navigate(R.id.action_responseLockerFragment_to_homeFragment)
        }
        else{
            Toast.makeText(requireContext(), "Kamu gagal memasukkan data ke DB tolong lengkapi form", Toast.LENGTH_SHORT).show()
        }
    }

    private fun inputCheck(Password: String, Time: String): Boolean{
        return !(TextUtils.isEmpty(Password) || TextUtils.isEmpty(Time))
    }

    private fun checkBox(idBox : Int, data: ResponseAPI, password: String, duration: Int, mAlertDialog: AlertDialog){
        RetrofitClient.instance.getRentBox(idBox).enqueue(object : Callback<List<ResponseAPI>> {
            override fun onResponse(call: Call<List<ResponseAPI>>, response: Response<List<ResponseAPI>>) {
                Log.d("p4", "response up " + response.body().toString())
                response.body()?.let { listcheck.addAll(it) }
                Log.d("p4", "id ${listcheck[0].noBox}")
                Log.d("p4", "isRent ${listcheck[0].isRent}")
                if(!listcheck[0].isRent!!){
                    updateBox(data, password, duration, mAlertDialog)
                }
                else{
                    Toast.makeText(requireContext(), "Box Sudah Tersewa", Toast.LENGTH_SHORT).show()
                    mAlertDialog.dismiss()
                    list.clear()
                    listcheck.clear()
                    getdata()
                }
            }

            override fun onFailure(call: Call<List<ResponseAPI>>, t: Throwable) {
                Log.d("p4", "fail up " + t.message.toString())
            }
        })
    }

    private fun updateBox(data: ResponseAPI, datapassword: String, dataduration: Int, mAlertDialog: AlertDialog){
        RetrofitClient.instance.patchBoxLocker(
                data.id!!,
                null,
                null,
                datapassword,
                dataduration,
                true,
                null,
                null
        ).enqueue(object : Callback<ResponseAPI> {
            override fun onResponse(call: Call<ResponseAPI>, response: Response<ResponseAPI>) {
                Log.d("p2", response.body().toString())
                insertDataToDB(data, datapassword, dataduration)
                val time = dataduration
//                Toast.makeText(requireContext(), "Kamu sukses memasukkan " + datapassword + " dan " + dataduration, Toast.LENGTH_SHORT).show()
                startService(time, data.noBox!!)
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

    private fun showLoading(state: Boolean) {
        if (state) {
            progressbar.visibility = View.VISIBLE
        } else {
            progressbar.visibility = View.GONE
        }
    }


}