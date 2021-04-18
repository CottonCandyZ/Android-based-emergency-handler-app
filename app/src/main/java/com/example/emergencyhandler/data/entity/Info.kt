package com.example.emergencyhandler.data.entity

import java.util.*


data class Info(
    val realName: String,
    val sex: String?,
    val birthdate: Date,
    val phone: String,
    val weight: Int?,
    val bloodType: String?,
    val medicalConditions: String?,
    val medicalNotes: String?,
    val allergy: String?,
    val medications: String?,
    val address: String?,
)

data class EmergencyContact(
    var relationship: String = "",
    var phone: String = "",
)

data class InfoWithEmergencyContact(
    val info: Info,
    val emergencyContacts: List<EmergencyContact>
)