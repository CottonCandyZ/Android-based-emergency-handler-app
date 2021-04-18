package com.example.emergencyhandler.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.emergencyhandler.data.entity.Call
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
    enum class STATUS {
        CHECK_SUCCESS, CHECK_ERROR
    }

    lateinit var errorMessage: String

    private val _status = MutableLiveData<STATUS>()
    val status: LiveData<STATUS> = _status

    private val _showInfo = MutableLiveData<InputData>()
    val showInfo: LiveData<InputData> = _showInfo

    private val _call = MutableLiveData<Call>()
    val call: LiveData<Call> = _call

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
                        SimpleDateFormat("yyyy/MM/dd", Locale.CHINA).format(birthdate)
                    inputInfo[PHONE] = phone
                    inputInfo[WEIGHT] = weight?.toString() ?: ""
                    inputInfo[BLOOD_TYPE] = bloodType ?: ""
                    inputInfo[MEDICAL_CONDITIONS] = medicalConditions ?: ""
                    inputInfo[MEDICAL_NOTES] = medicalConditions ?: ""
                    inputInfo[ALLERGY] = allergy ?: ""
                    inputInfo[MEDICATIONS] = medications ?: ""
                    inputInfo[ADDRESS] = address ?: ""
                }
            }
            val emergencyContacts = result.emergencyContacts
            emergencyContacts.forEach {
                inputData.emergencyNumber.add(it)
            }
            _showInfo.value = inputData
        }
    }

    fun getCall(callId: String) {
        viewModelScope.launch {
            callRepository.getCallInfoById(callId).collect {
                _call.value = it[0]
            }
        }
    }

    fun checkStatus() {
        viewModelScope.launch {
            try {
                callRepository.checkStatus(call.value!!.id)
                _status.value = STATUS.CHECK_SUCCESS
            } catch (e: Exception) {
                errorMessage = getErrorMessage(e)
                _status.value = STATUS.CHECK_ERROR
            }


        }
    }

    class InputData(
        val inputInfo: Array<String>,
        val emergencyNumber: ArrayList<EmergencyContact>
    )

}