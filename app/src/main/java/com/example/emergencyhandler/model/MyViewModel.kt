package com.example.emergencyhandler.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.example.emergencyhandler.data.local.CallRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class MyViewModel @Inject constructor(
    private val callRepository: CallRepository
) : ViewModel() {
    val call = callRepository.getCallInfo().asLiveData()
}