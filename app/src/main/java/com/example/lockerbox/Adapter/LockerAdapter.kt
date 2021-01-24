package com.example.lockerbox.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.lockerbox.R
import com.example.lockerbox.api.ResponseAPI
import kotlinx.android.synthetic.main.fragment_response_locker.view.*
import kotlinx.android.synthetic.main.item_box.view.*
import kotlinx.android.synthetic.main.listapi.view.*

class LockerAdapter(private val listBox: ArrayList<ResponseAPI>) : RecyclerView.Adapter<LockerAdapter.ListViewHolder>() {

    private lateinit var onItemClickCallback: OnItemClickCallback

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    interface OnItemClickCallback {
        fun onItemClicked(data: ResponseAPI)
    }

    inner class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var buttonRent: Button = itemView.findViewById(R.id.Button_Rent)
        fun bind(lockerResponse: ResponseAPI){
            with(itemView){
//                val text : String = "ID: ${lockerResponse.id}\n" +
//                        "ID Locker: ${lockerResponse.codeLocker}\n" +
//                        "No Box: ${lockerResponse.noBox}\n" +
//                        "Password: ${lockerResponse.password}"

//                BoxText.text = text
                if(lockerResponse.isRent == true){
                    buttonRent.isVisible = false
                }
                NoBox.text = lockerResponse.noBox.toString()

            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val view : View = LayoutInflater.from(parent.context).inflate(R.layout.item_box, parent, false)
        return  ListViewHolder(view)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        holder.bind(listBox[position])
        holder.buttonRent.setOnClickListener{
            onItemClickCallback.onItemClicked(listBox[holder.adapterPosition])
        }
    }

    override fun getItemCount(): Int = listBox.size
}