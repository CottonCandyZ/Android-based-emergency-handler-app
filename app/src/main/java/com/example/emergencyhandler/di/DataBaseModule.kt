package com.example.emergencyhandler.di

import android.content.Context
import androidx.room.Room
import com.example.emergencyhandler.data.local.AppDatabase
import com.example.emergencyhandler.data.local.dao.CallDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
class DataBaseModule {
    @Provides
    fun provideCallDao(appDatabase: AppDatabase): CallDao {
        return appDatabase.callDao()
    }

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext appContext: Context): AppDatabase {
        return Room.databaseBuilder(
            appContext,
            AppDatabase::class.java,
            "app_database"
        ).build()
    }

}