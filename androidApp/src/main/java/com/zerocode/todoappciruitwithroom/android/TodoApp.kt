package com.zerocode.todoappciruitwithroom.android

import android.app.Application
import com.zerocode.todoappciruitwithroom.util.initKoinAndroid
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger

class TodoApp : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoinAndroid {
            androidLogger()
            androidContext(applicationContext)
        }
    }
}