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
    private val _state = MutableLiveData<STATE>()
    val state: LiveData<STATE> = _state
    lateinit var errorMessage: String


    init {
        refresh()
        initLiveQuery()
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


    val call = callRepository.getCallInfo().asLiveData()


    enum class STATE {
        FETCH_SUCCESS, FETCH_ERROR, REFRESH_COMPLETE, REFRESH_ERROR
    }


    override fun onCleared() {
        super.onCleared()
        callRepository.unsubscribe()
    }
}