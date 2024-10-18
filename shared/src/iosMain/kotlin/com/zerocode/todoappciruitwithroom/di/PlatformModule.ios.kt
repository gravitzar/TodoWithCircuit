package com.zerocode.todoappciruitwithroom.di

import com.zerocode.todoappciruitwithroom.storage.createDataStore
import com.zerocode.todoappciruitwithroom.storage.createDatabase
import com.zerocode.todoappciruitwithroom.storage.getDatabaseBuilder
import org.koin.core.module.Module
import org.koin.dsl.module

actual val platformModule: Module = module {
    single { createDatabase(getDatabaseBuilder()) }
    single { createDataStore() }
}