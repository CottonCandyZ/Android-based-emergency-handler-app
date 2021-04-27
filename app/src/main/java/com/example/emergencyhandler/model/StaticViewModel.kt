package com.example.emergencyhandler.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.emergencyhandler.data.local.repository.CallRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject


@HiltViewModel
class StaticViewModel @Inject constructor(
    private val callRepository: CallRepository
) : ViewModel() {
    private val _number = MutableLiveData<Int>()
    val number: LiveData<Int> = _number

    private val _state = MutableLiveData<STATE>()
    val state: LiveData<STATE> = _state

    lateinit var customDate: Pair<Long, Long>
    lateinit var inputDate: Pair<Long, Long>

    init {
        setState(STATE.TODAY)
    }


    fun setState(state: STATE) {
        when (state) {
            STATE.TODAY -> {
                viewModelScope.launch {
                    _number.value = callRepository.getTodayCallNumber()
                }
            }
            STATE.WEEK -> {
                viewModelScope.launch {
                    _number.value = callRepository.getWeedCallNumber()
                }
            }
            STATE.MONTH -> {
                viewModelScope.launch {
                    _number.value = callRepository.getMonthCallNumber()
                }
            }
            STATE.YEAR -> {
                viewModelScope.launch {
                    _number.value = callRepository.getYearCallNumber()
                }
            }
            STATE.CUSTOM -> {
                viewModelScope.launch {
                    val calendar = Calendar.getInstance()
                    calendar.timeZone = TimeZone.getTimeZone("GMT+8:00")
                    calendar.timeInMillis = inputDate.first
                    calendar.set(Calendar.HOUR_OF_DAY, 0)
                    val start = calendar.timeInMillis
                    calendar.timeInMillis = inputDate.second
                    calendar.set(Calendar.HOUR_OF_DAY, 23)
                    calendar.set(Calendar.MINUTE, 59)
                    calendar.set(Calendar.SECOND, 59)
                    calendar.set(Calendar.MILLISECOND, 999)
                    val end = calendar.timeInMillis
                    customDate = Pair(start, end)
                    _number.value = callRepository.getCallNumber(start, end)
                }
            }
        }
        _state.value = state
    }


    enum class STATE {
        CUSTOM, TODAY, WEEK, MONTH, YEAR
    }

}