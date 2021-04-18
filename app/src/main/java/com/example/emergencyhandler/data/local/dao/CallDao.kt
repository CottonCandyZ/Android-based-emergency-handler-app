package com.example.emergencyhandler.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.emergencyhandler.data.entity.Call
import kotlinx.coroutines.flow.Flow


@Dao
interface CallDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCall(vararg call: Call)


    @Query("SELECT * FROM call ORDER BY ID DESC")
    fun getCall(): Flow<List<Call>>

    // 通过 ID 删除
    @Query("DELETE FROM call WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("SELECT * FROM call WHERE id = :id")
    fun getCallById(id: String): Flow<List<Call>>


    @Query("DELETE FROM call")
    fun nukeTable()
}