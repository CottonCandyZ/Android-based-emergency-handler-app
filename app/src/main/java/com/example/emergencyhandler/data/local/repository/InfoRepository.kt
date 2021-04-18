package com.example.emergencyhandler.data.local.repository

import com.example.emergencyhandler.data.entity.InfoWithEmergencyContact
import com.example.emergencyhandler.data.remote.InfoService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject


class InfoRepository @Inject constructor(
    private val infoService: InfoService
) {
    suspend fun getInfoWithEmergencyContact(infoId: String): InfoWithEmergencyContact =
        withContext(Dispatchers.IO) {
            return@withContext infoService.getInfoWithEmergencyContact(infoId)
        }

}