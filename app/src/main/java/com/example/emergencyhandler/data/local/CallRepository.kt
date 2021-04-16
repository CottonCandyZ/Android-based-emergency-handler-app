package com.example.emergencyhandler.data.local

import cn.leancloud.AVObject
import cn.leancloud.livequery.AVLiveQueryEventHandler
import com.example.emergencyhandler.convertAVObjectToCall
import com.example.emergencyhandler.data.entity.Call
import com.example.emergencyhandler.data.local.dao.CallDao
import com.example.emergencyhandler.data.remote.WebService
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

class CallRepository @Inject constructor(
    private val callDao: CallDao,
    private val webService: WebService
) {
    init {
        webService.autoFetchData(object : AVLiveQueryEventHandler() {
            override fun onObjectCreated(avObject: AVObject) {
                super.onObjectCreated(avObject)
                update(avObject)
            }

            override fun onObjectUpdated(avObject: AVObject, updateKeyList: MutableList<String>?) {
                super.onObjectUpdated(avObject, updateKeyList)
                update(avObject)
            }

            override fun onObjectDeleted(objectId: String) {
                super.onObjectDeleted(objectId)
                MainScope().launch {
                    callDao.deleteById(objectId)
                }

            }
        })
    }

    fun update(remote: AVObject) {
        MainScope().launch {
            callDao.insertCall(convertAVObjectToCall(remote))
        }
    }

    fun getCallInfo(): Flow<List<Call>> {
        MainScope().launch {
            refreshAbstractCall()
        }
        return callDao.getCall()
    }

    private suspend fun refreshAbstractCall() {
        val list = webService.getCallInfo()
        callDao.nukeTable()
        callDao.insertCall(*list.toTypedArray())
    }

}