package com.example.emergencyhandler.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.emergencyhandler.data.entity.Call
import com.example.emergencyhandler.data.local.repository.CallRepository
import com.example.emergencyhandler.getErrorMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class MyViewModel @Inject constructor(
    private val callRepository: CallRepository
) : ViewModel() {
    private val _state = MutableLiveData<STATE>()
    val state: LiveData<STATE> = _state
    private val _call = MutableLiveData<List<Call>>()
    val call: LiveData<List<Call>> = _call
    lateinit var errorMessage: String
    private var callJob: Job


    init {
        refresh()
        initLiveQuery()
        callJob = viewModelScope.launch {
            callRepository.getCallInfo().collect {
                _call.value = it
            }
        }
    }

    fun setFilterStatus(filter: String) {
        callJob.cancel()
        callJob = viewModelScope.launch {
            if (filter == "显示全部") {
                callRepository.getCallInfo().collect {
                    _call.value = it
                }
            } else {
                callRepository.getCallInfoByFilter(filter).collect {
                    _call.value = it
                }
            }
        }
    }

    private fun initLiveQuery() {
        callRepository.init()
    }

    private fun refresh() {
        viewModelScope.launch {
            try {
                callRepository.refreshCall()
                _state.value = STATE.FETCH_SUCCESS
            } catch (e: Exception) {
                errorMessage = getErrorMessage(e)
                _state.value = STATE.FETCH_ERROR
            }
        }
    }


    fun refreshManually() {
        viewModelScope.launch {
            try {
                callRepository.refreshCall()
                _state.value = STATE.REFRESH_COMPLETE
            } catch (e: Exception) {
                _state.value = STATE.REFRESH_ERROR
            }

        }
    }


    enum class STATE {
        FETCH_SUCCESS, FETCH_ERROR, REFRESH_COMPLETE, REFRESH_ERROR
    }


    override fun onCleared() {
        super.onCleared()
        callRepository.unsubscribe()
    }
}