package com.example.emergencyhandler.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.emergencyhandler.data.entity.Call
import com.example.emergencyhandler.data.entity.Converters
import com.example.emergencyhandler.data.local.dao.CallDao


@Database(
    entities = [Call::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun callDao(): CallDao
}