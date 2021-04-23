package com.example.emergencyhandler.data.remote

import cn.leancloud.AVObject
import cn.leancloud.AVQuery
import cn.leancloud.livequery.AVLiveQuery
import com.example.emergencyhandler.convertAVObjectToCall
import com.example.emergencyhandler.data.entity.Call
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class CallService @Inject constructor() {
    val callLiveQuery: AVLiveQuery

    init {
        val query = AVQuery<AVObject>("Call")
        query.whereNotEqualTo("status", "")
        callLiveQuery = AVLiveQuery.initWithQuery(query)
    }

    fun getCallInfo(): List<Call> {
        val query = AVQuery<AVObject>("Call")
        val list = query.find()
        val resultList: ArrayList<Call> = arrayListOf()
        list.forEach {
            resultList.add(convertAVObjectToCall(it))
        }
        return resultList
    }

    fun checkStatus(callId: String) {
        val changeItem = AVObject.createWithoutData("Call", callId)
        changeItem.put("status", "已处理")
        changeItem.put("handler", "CottonCandyZ")
        changeItem.put("responseTime", Calendar.getInstance().time)
        changeItem.save()
    }
}