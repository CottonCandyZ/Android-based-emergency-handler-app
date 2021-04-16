package com.example.emergencyhandler.data.local.dao

import androidx.room.*
import com.example.emergencyhandler.data.entity.Call
import kotlinx.coroutines.flow.Flow


@Dao
interface CallDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCall(vararg call: Call)

    @Update
    suspend fun updateCall(vararg call: Call)

    @Query("SELECT * FROM call ORDER BY ID DESC")
    fun getCall(): Flow<List<Call>>

    // 通过 ID 删除
    @Query("DELETE FROM call WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("DELETE FROM call")
    suspend fun nukeTable()
}