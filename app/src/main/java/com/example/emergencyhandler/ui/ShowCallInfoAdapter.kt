package com.example.emergencyhandler.ui

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.emergencyhandler.data.entity.Call
import com.example.emergencyhandler.databinding.CallInfoItemBinding

class ShowCallInfoAdapter : ListAdapter<Call, ShowCallInfoAdapter.MyViewHolder>(DIFFCALLBACK) {

    class MyViewHolder(
        private val binding: CallInfoItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(call: Call) {
            with(binding) {
                patient.text = call.patientName
                callerAccount.text = call.callerAccount
                location.text = call.locationName
                if (call.status == "呼救中") {
                    status.setTextColor(Color.RED)
                } else if (call.status == "已取消") {
                    status.setTextColor(Color.GRAY)
                } else if (call.status == "已处理") {
                    status.setTextColor(Color.BLUE)
                }
                status.text = call.status
            }
        }

        companion object {
            fun create(parent: ViewGroup) =
                CallInfoItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(MyViewHolder.create(parent))
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(getItem(position))
    }


    object DIFFCALLBACK : DiffUtil.ItemCallback<Call>() {
        override fun areItemsTheSame(oldItem: Call, newItem: Call): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Call, newItem: Call): Boolean {
            return oldItem == newItem
        }
    }
}