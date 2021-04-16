package com.example.emergencyhandler

import cn.leancloud.AVObject
import com.example.emergencyhandler.data.entity.Call

fun convertAVObjectToCall(avObject: AVObject): Call {
    return Call(
        id = avObject.objectId,
        callerAccountId = avObject.getString("callerAccountId"),
        callerAccount = avObject.getString("callerAccount"),
        handler = avObject.getString("handler"),
        locationCoordinate = avObject.getString("locationCoordinate"),
        locationName = avObject.getString("locationName"),
        patientId = avObject.getString("patientId"),
        patientName = avObject.getString("patientName"),
        responseTime = avObject.getDate("responseTime"),
        status = avObject.getString("status"),
        createTime = avObject.createdAt
    )
}