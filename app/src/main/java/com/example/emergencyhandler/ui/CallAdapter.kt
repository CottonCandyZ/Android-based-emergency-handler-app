package com.example.emergencyhandler.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.emergencyhandler.data.entity.CallPhone
import com.example.emergencyhandler.databinding.CallPhoneItemsBinding

class CallAdapter constructor(
    private val callList: List<CallPhone>
) : RecyclerView.Adapter<CallAdapter.CallViewHolder>() {
    class CallViewHolder(
        private val binding: CallPhoneItemsBinding,
        private val context: Context,
    ) :
        RecyclerView.ViewHolder(binding.root) {
        private lateinit var callPhone: CallPhone

        init {
            binding.container.setOnClickListener {
                val callIntent =
                    Intent(
                        Intent.ACTION_CALL,
                        Uri.parse("tel:" + callPhone.phone)
                    )
                context.startActivity(callIntent)
            }
        }

        fun bind(item: CallPhone) {
            callPhone = item
            binding.callHint.text = item.hint
        }

        companion object {
            fun create(parent: ViewGroup) =
                CallPhoneItemsBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CallViewHolder {
        return CallViewHolder(
            CallViewHolder.create(parent),
            parent.context
        )
    }

    override fun onBindViewHolder(holder: CallViewHolder, position: Int) {
        holder.bind(callList[position])
    }

    override fun getItemCount() = callList.size
}