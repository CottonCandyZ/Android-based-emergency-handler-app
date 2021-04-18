package com.example.emergencyhandler.data.remote

import cn.leancloud.AVObject
import cn.leancloud.AVQuery
import com.example.emergencyhandler.data.entity.EmergencyContact
import com.example.emergencyhandler.data.entity.Info
import com.example.emergencyhandler.data.entity.InfoWithEmergencyContact
import javax.inject.Inject

class InfoService @Inject constructor() {

    fun getInfoWithEmergencyContact(id: String): InfoWithEmergencyContact {
        val queryInfo = AVQuery<AVObject>("Info")
        queryInfo.whereEqualTo("objectId", id)
        val infoResult = queryInfo.find()
        var info: Info? = null
        infoResult.forEach {
            info = Info(
                realName = it.getString("realName"),
                sex = it.getString("sex"),
                it.getDate("birthdate"),
                phone = it.getString("phone"),
                weight = it.getInt("weight"),
                bloodType = it.getString("bloodType"),
                medicalConditions = it.getString("medicalConditions"),
                medicalNotes = it.getString("medicalNotes"),
                allergy = it.getString("allergy"),
                medications = it.getString("medications"),
                address = it.getString("address"),
            )
        }

        // 查紧急联系人
        val queryEmergencyContact = AVQuery<AVObject>("EmergencyContact")
        queryEmergencyContact.whereEqualTo("infoId", id)
        val emergencyContacts: ArrayList<EmergencyContact> = arrayListOf()
        val emergencyContactsResult = queryEmergencyContact.find()
        emergencyContactsResult.forEach {
            val emergencyContact = EmergencyContact(
                relationship = it.get("relationship") as String,
                phone = it.get("phone") as String
            )
            emergencyContacts.add(emergencyContact)
        }



        return InfoWithEmergencyContact(
            info!!,
            emergencyContacts
        )

    }
}