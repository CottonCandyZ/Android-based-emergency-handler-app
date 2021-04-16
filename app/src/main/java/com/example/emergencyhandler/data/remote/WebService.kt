package com.example.emergencyhandler.data.remote

import cn.leancloud.AVException
import cn.leancloud.AVObject
import cn.leancloud.AVQuery
import cn.leancloud.livequery.AVLiveQuery
import cn.leancloud.livequery.AVLiveQueryEventHandler
import cn.leancloud.livequery.AVLiveQuerySubscribeCallback
import com.example.emergencyhandler.convertAVObjectToCall
import com.example.emergencyhandler.data.entity.Call
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class WebService @Inject constructor() {
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

    suspend fun getCallInfo(): List<Call> = withContext(Dispatchers.IO) {
        val query = AVQuery<AVObject>("Call")
        val list = query.find()
        val resultList: ArrayList<Call> = arrayListOf()
        list.forEach {
            resultList.add(convertAVObjectToCall(it))
        }
        return@withContext resultList
    }
}