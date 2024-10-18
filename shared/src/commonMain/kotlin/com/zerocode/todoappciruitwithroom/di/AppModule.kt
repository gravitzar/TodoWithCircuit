package com.zerocode.todoappciruitwithroom.di

import com.zerocode.todoappciruitwithroom.TodoRepository
import com.zerocode.todoappciruitwithroom.storage.DataStoreManager
import org.koin.core.module.Module
import org.koin.dsl.module

fun appModule(): List<Module> = listOf(
    platformModule,
    module {
        single { DataStoreManager(get()) }
        single { TodoRepository(get(), get()) }
    },
)
