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
import java.util.*
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

    fun getCallInfoByFilter(filter: String): Flow<List<Call>> {
        return callDao.getCallByFilter(filter)
    }

    fun getCallInfo(): Flow<List<Call>> {
        return callDao.getCall()
    }

    fun getCallInfoById(id: String): Flow<List<Call>> {
        return callDao.getCallById(id)
    }


    suspend fun getCallNumber(startDate: Long, endDate: Long): Int =
        withContext(Dispatchers.IO) {
            return@withContext callDao.getCallNumber(startDate, endDate)
        }


    suspend fun getTodayCallNumber(): Int = withContext(Dispatchers.IO) {
        val calendar = Calendar.getInstance()
        calendar.timeZone = TimeZone.getTimeZone("GMT+8:00")
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val start = calendar.timeInMillis
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.SECOND, 59)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.MILLISECOND, 999)
        val end = calendar.timeInMillis
        return@withContext callDao.getCallNumber(start, end)
    }

    suspend fun getWeedCallNumber(): Int = withContext(Dispatchers.IO) {
        val calendar = Calendar.getInstance()
        calendar.timeZone = TimeZone.getTimeZone("GMT+8:00")
        var dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1
        if (dayOfWeek == 0) {
            dayOfWeek = 7
        }
        calendar.add(Calendar.DATE, -dayOfWeek + 1)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val start = calendar.timeInMillis
        calendar.add(Calendar.DATE, 6)
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.SECOND, 59)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.MILLISECOND, 999)
        val end = calendar.timeInMillis
        return@withContext callDao.getCallNumber(start, end)
    }

    suspend fun getMonthCallNumber(): Int = withContext(Dispatchers.IO) {
        val calendar = Calendar.getInstance()
        calendar.timeZone = TimeZone.getTimeZone("GMT+8:00")
        calendar.add(Calendar.YEAR, 0)
        calendar.add(Calendar.MONTH, 0)
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val start = calendar.timeInMillis
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.SECOND, 59)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.MILLISECOND, 999)
        val end = calendar.timeInMillis
        return@withContext callDao.getCallNumber(start, end)
    }

    suspend fun getYearCallNumber(): Int = withContext(Dispatchers.IO) {
        val calendar = Calendar.getInstance()
        calendar.timeZone = TimeZone.getTimeZone("GMT+8:00")
        calendar.add(Calendar.YEAR, 0)
        calendar.add(Calendar.DATE, 0)
        calendar.add(Calendar.MONTH, 0)
        calendar.set(Calendar.DAY_OF_YEAR, 1)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val start = calendar.timeInMillis
        calendar.roll(Calendar.DAY_OF_YEAR, -1)
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.SECOND, 59)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.MILLISECOND, 999)
        val end = calendar.timeInMillis
        return@withContext callDao.getCallNumber(start, end)
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