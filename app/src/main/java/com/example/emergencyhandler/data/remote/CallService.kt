package com.example.emergencyhandler.data.remote

import cn.leancloud.AVException
import cn.leancloud.AVObject
import cn.leancloud.AVQuery
import cn.leancloud.livequery.AVLiveQuery
import cn.leancloud.livequery.AVLiveQueryEventHandler
import cn.leancloud.livequery.AVLiveQuerySubscribeCallback
import com.example.emergencyhandler.convertAVObjectToCall
import com.example.emergencyhandler.data.entity.Call
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class CallService @Inject constructor() {
    fun autoFetchData(handler: AVLiveQueryEventHandler) {
        val query = AVQuery<AVObject>("Call")
        query.whereNotEqualTo("status", "")
        val liveQuery = AVLiveQuery.initWithQuery(query)
        liveQuery.setEventHandler(handler)
        liveQuery.subscribeInBackground(object : AVLiveQuerySubscribeCallback() {
            override fun done(e: AVException?) {
            }
        })
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