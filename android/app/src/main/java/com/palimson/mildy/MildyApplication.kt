package com.palimson.mildy

import android.app.Application
import androidx.room.Room
import com.palimson.mildy.data.local.AppDatabase

class MildyApplication : Application() {
    lateinit var database: AppDatabase
        private set

    override fun onCreate() {
        super.onCreate()
        instance = this
        database = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "mildy_studio.db"
        ).fallbackToDestructiveMigration().build()
    }

    companion object {
        lateinit var instance: MildyApplication
            private set
    }
}
