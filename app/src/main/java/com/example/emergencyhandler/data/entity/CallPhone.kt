package com.example.emergencyhandler.data.entity

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class CallPhone(
    var phone: String,
    val hint: String,
) : Parcelable
