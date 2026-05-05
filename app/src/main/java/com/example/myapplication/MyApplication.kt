package com.example.myapplication

import android.app.Application
import androidx.room.Room
import com.example.myapplication.data.local.AppDatabase

class MyApplication : Application() {
    
    companion object {
        lateinit var database: AppDatabase
            private set
    }

    override fun onCreate() {
        super.onCreate()
        database = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "marketplace_db"
        ).fallbackToDestructiveMigration().build()
    }
}
