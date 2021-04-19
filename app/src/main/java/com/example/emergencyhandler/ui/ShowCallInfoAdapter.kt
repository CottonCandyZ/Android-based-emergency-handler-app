package com.example.emergencyhandler.ui

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.emergencyhandler.R
import com.example.emergencyhandler.data.entity.Call
import com.example.emergencyhandler.databinding.CallInfoItemBinding
import java.text.SimpleDateFormat
import java.util.*

class ShowCallInfoAdapter : ListAdapter<Call, ShowCallInfoAdapter.MyViewHolder>(DIFFCALLBACK) {

    class MyViewHolder(
        private val binding: CallInfoItemBinding,
        val onClickListener: OnClickListener
    ) : RecyclerView.ViewHolder(binding.root) {
        init {
            onClickListener.setBinding(binding)
            binding.container.setOnClickListener(onClickListener)
        }

        fun bind(call: Call) {
            with(binding) {
                patient.text = call.patientName
                callerAccount.text = call.callerAccount
                handler.text = call.handler ?: "尚未处理"
                createTime.text = convertDateToString(call.createTime)
                responseTime.text =
                    if (call.responseTime == null) "尚未处理" else convertDateToString(call.responseTime)
                if (call.locationCoordinate == null) {
                    location.text = "尚未获得"
                } else {
                    location.text = call.locationName
                }
                when (call.status) {
                    "呼救中" -> {
                        status.setTextColor(Color.RED)
                    }
                    "已取消" -> {
                        status.setTextColor(Color.GRAY)
                    }
                    "已处理" -> {
                        status.setTextColor(Color.BLUE)
                    }
                }
                status.text = call.status
            }
        }

        private fun convertDateToString(date: Date): String {
            val format = "yyyy.MM.dd 'at' HH:mm:ss"
            return SimpleDateFormat(format, Locale.CHINA).format(date.time)
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
        return MyViewHolder(
            MyViewHolder.create(parent),
            OnClickListener()
        )
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(getItem(position))
        holder.onClickListener.setCall(getItem(position))
    }

    inner class OnClickListener : View.OnClickListener {
        private lateinit var binding: CallInfoItemBinding
        private var call: Call? = null

        fun setCall(call: Call) {
            this.call = call
        }

        fun setBinding(binding: CallInfoItemBinding) {
            this.binding = binding
        }

        override fun onClick(p0: View?) {
            Bundle().apply {
                putString("INFO_ID", call!!.patientId)
                putString("CALL_ID", call!!.id)
                binding.root.findNavController()
                    .navigate(R.id.action_showFragment_to_detailFragment, this)
            }
        }
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