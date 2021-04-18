package com.example.emergencyhandler.model

import androidx.lifecycle.*
import com.example.emergencyhandler.data.local.repository.CallRepository
import com.example.emergencyhandler.getErrorMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class MyViewModel @Inject constructor(
    private val callRepository: CallRepository
) : ViewModel() {
    private val _status = MutableLiveData<STATUS>()
    val status: LiveData<STATUS> = _status
    lateinit var errorMessage: String


    init {
        refresh()
        initLiveQuery()
    }


    fun initLiveQuery() {
        callRepository.init()
    }

    fun refresh() {
        viewModelScope.launch {
            try {
                callRepository.refreshCall()
                _status.value = STATUS.FETCH_SUCCESS
            } catch (e: Exception) {
                errorMessage = getErrorMessage(e)
                _status.value = STATUS.FETCH_ERROR
            }
        }
    }

    fun refreshManually() {
        viewModelScope.launch {
            try {
                callRepository.refreshCall()
                _status.value = STATUS.REFRESH_COMPLETE
            } catch (e: Exception) {
                _status.value = STATUS.REFRESH_ERROR
            }

        }
    }


    val call = callRepository.getCallInfo().asLiveData()


    enum class STATUS {
        FETCH_SUCCESS, FETCH_ERROR, REFRESH_COMPLETE, REFRESH_ERROR
    }
}