package com.example.lockerbox.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.lockerbox.R
import com.example.lockerbox.Room.LockerBox
import com.example.lockerbox.api.ResponseAPI
import kotlinx.android.synthetic.main.box_layout.view.*
import kotlinx.android.synthetic.main.fragment_home.view.*
import kotlinx.android.synthetic.main.item_box.view.*
import kotlinx.android.synthetic.main.item_box.view.NoBox

class HomeAdapter(private var boxList: List<LockerBox>): RecyclerView.Adapter<HomeAdapter.ListViewHolder>() {

    private lateinit var onItemClickCallback: OnItemClickCallback

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    interface OnItemClickCallback {
        fun onItemClicked(data: LockerBox)
    }

    inner class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        var buttonRent: Button = itemView.findViewById(R.id.Button_Rent)
        fun bind(boxResponse: LockerBox){
            with(itemView){
//                val text : String = "ID: ${lockerResponse.id}\n" +
//                        "ID Locker: ${lockerResponse.codeLocker}\n" +
//                        "No Box: ${lockerResponse.noBox}\n" +
//                        "Password: ${lockerResponse.password}"

//                BoxText.text = text
//                if(lockerResponse.isRent == true){
//                    buttonRent.isVisible = false
//                }
                NoBox.text = boxResponse.noBox.toString()
                CodeLocker.text = boxResponse.codeLocker.toString()
                duration_time.text = boxResponse.duration.toString() + " Jam"
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val view : View = LayoutInflater.from(parent.context).inflate(R.layout.box_layout, parent, false)
        return  ListViewHolder(view)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        holder.bind(boxList[position])
        holder.itemView.setOnClickListener{
//            Toast.makeText(holder.itemView.context, "Kamu memilih " + boxList[position].codeLocker +"   " + boxList[position].noBox, Toast.LENGTH_SHORT).show()
            onItemClickCallback.onItemClicked(boxList[holder.adapterPosition])
        }
    }

    override fun getItemCount(): Int {
        return boxList.size
    }

    fun setData(box: List<LockerBox>){
        this.boxList = box
        notifyDataSetChanged()
    }
}