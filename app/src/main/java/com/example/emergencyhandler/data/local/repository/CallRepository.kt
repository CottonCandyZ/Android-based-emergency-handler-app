package com.example.emergencyhandler.data.local.repository

import cn.leancloud.AVException
import cn.leancloud.AVObject
import cn.leancloud.livequery.AVLiveQueryEventHandler
import cn.leancloud.livequery.AVLiveQuerySubscribeCallback
import com.example.emergencyhandler.convertAVObjectToCall
import com.example.emergencyhandler.data.entity.Call
import com.example.emergencyhandler.data.local.dao.CallDao
import com.example.emergencyhandler.data.remote.CallService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class CallRepository @Inject constructor(
    private val callDao: CallDao,
    private val callService: CallService
) {
    fun init() {
        callService.callLiveQuery.setEventHandler(object : AVLiveQueryEventHandler() {
            override fun onObjectCreated(avObject: AVObject) {
                super.onObjectCreated(avObject)
                MainScope().launch {
                    update(avObject)
                }
            }

            override fun onObjectUpdated(avObject: AVObject, updateKeyList: MutableList<String>?) {
                super.onObjectUpdated(avObject, updateKeyList)
                MainScope().launch {
                    update(avObject)
                }
            }

            override fun onObjectDeleted(objectId: String) {
                super.onObjectDeleted(objectId)
                MainScope().launch {
                    callDao.deleteById(objectId)
                }

            }
        })
        callService.callLiveQuery.subscribeInBackground(object : AVLiveQuerySubscribeCallback() {
            override fun done(e: AVException?) {

            }
        })
    }

    fun unsubscribe() {
        callService.callLiveQuery.unsubscribeInBackground(object : AVLiveQuerySubscribeCallback() {
            override fun done(e: AVException?) {
            }
        })
    }

    suspend fun update(remote: AVObject) {
        withContext(Dispatchers.IO) {
            callDao.insertCall(convertAVObjectToCall(remote))
        }
    }

    fun getCallInfo(): Flow<List<Call>> {
        return callDao.getCall()
    }

    fun getCallInfoById(id: String): Flow<List<Call>> {
        return callDao.getCallById(id)
    }

    suspend fun refreshCall() {
        withContext(Dispatchers.IO) {
            val list = callService.getCallInfo()
            callDao.nukeTable()
            callDao.insertCall(*list.toTypedArray())
        }
    }

    suspend fun checkStatus(callId: String) {
        withContext(Dispatchers.IO) {
            callService.checkStatus(callId)
        }
    }

}