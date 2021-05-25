package com.example.emergencyhandler.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.emergencyhandler.data.entity.Call
import com.example.emergencyhandler.data.entity.CallPhone
import com.example.emergencyhandler.data.entity.EmergencyContact
import com.example.emergencyhandler.data.local.repository.CallRepository
import com.example.emergencyhandler.data.local.repository.InfoRepository
import com.example.emergencyhandler.getErrorMessage
import com.example.emergencyhandler.ui.ShowInfoAdapter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject


@HiltViewModel
class InfoViewModel @Inject constructor(
    private val infoRepository: InfoRepository,
    private val callRepository: CallRepository
) : ViewModel() {
    enum class STATE {
        CHECK_SUCCESS, CHECK_ERROR
    }

    lateinit var errorMessage: String

    private val _state = MutableLiveData<STATE>()
    val state: LiveData<STATE> = _state

    private val _showInfo = MutableLiveData<InputData>()
    val showInfo: LiveData<InputData> = _showInfo

    private val _call = MutableLiveData<Call>()
    val call: LiveData<Call> = _call

    val callList = arrayListOf<CallPhone>(
        CallPhone("", "呼救账户"),
        CallPhone("", "呼救人")
    )

    fun fetchInfo(infoId: String) {
        viewModelScope.launch {
            val inputData = InputData(Array(11) { "" }, arrayListOf())
            val result = infoRepository.getInfoWithEmergencyContact(infoId)
            val info = result.info
            val inputInfo = inputData.inputInfo
            info.run {
                with(ShowInfoAdapter.InputHint) {
                    inputInfo[REAL_NAME] = realName
                    inputInfo[SEX] = sex ?: ""
                    inputInfo[BIRTHDATE] =
                        SimpleDateFormat(
                            "yyyy/MM/dd",
                            Locale.CHINA
                        ).format(birthdate) + "（年龄：${getAge(birthdate)}）"
                    inputInfo[PHONE] = phone
                    inputInfo[WEIGHT] = weight?.toString() ?: ""
                    inputInfo[BLOOD_TYPE] = bloodType ?: ""
                    inputInfo[MEDICAL_CONDITIONS] = medicalConditions ?: ""
                    inputInfo[MEDICAL_NOTES] = medicalConditions ?: ""
                    inputInfo[ALLERGY] = allergy ?: ""
                    inputInfo[MEDICATIONS] = medications ?: ""
                    inputInfo[ADDRESS] = address ?: ""
                    callList[1].phone = "+86$phone"
                }
            }
            val emergencyContacts = result.emergencyContacts
            val add = callList.size <= 2
            emergencyContacts.forEach {
                inputData.emergencyNumber.add(it)
                if (add) {
                    callList.add(CallPhone("+86${it.phone}", "呼救人的${it.relationship}"))
                }
            }
            _showInfo.value = inputData
        }
    }

    fun getCall(callId: String) {
        viewModelScope.launch {
            callRepository.getCallInfoById(callId).collect {
                _call.value = it[0]
                callList[0].phone = it[0].callerAccount
            }
        }
    }

    fun checkStatus() {
        viewModelScope.launch {
            try {
                callRepository.checkStatus(call.value!!.id)
                _state.value = STATE.CHECK_SUCCESS
            } catch (e: Exception) {
                errorMessage = getErrorMessage(e)
                _state.value = STATE.CHECK_ERROR
            }


        }
    }

    private fun getAge(birthday: Date): Int {
        val now = Calendar.getInstance()
        val born = Calendar.getInstance()
        now.timeZone = TimeZone.getTimeZone("GMT+8:00")
        born.timeZone = TimeZone.getTimeZone("GMT+8:00")
        now.time = Date()
        born.time = birthday
        var age = now[Calendar.YEAR] - born[Calendar.YEAR]
        if (now[Calendar.DAY_OF_YEAR] < born[Calendar.DAY_OF_YEAR]) {
            age -= 1
        }
        return age
    }

    class InputData(
        val inputInfo: Array<String>,
        val emergencyNumber: ArrayList<EmergencyContact>
    )

}