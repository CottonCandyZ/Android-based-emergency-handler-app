package com.example.emergencyhandler.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import java.util.*


@Entity(tableName = "call")
data class Call(
    @PrimaryKey val id: String,
    val callerAccountId: String,
    val callerAccount: String,
    val handler: String?,
    val locationCoordinate: String,
    val locationName: String,
    val patientId: String,
    val patientName: String,
    val responseTime: Date?,
    val status: String,
    val createTime: Date,
)


class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return if (value == null) null else Date(value)
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }
}