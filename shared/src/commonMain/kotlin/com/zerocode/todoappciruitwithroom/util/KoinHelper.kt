package com.zerocode.todoappciruitwithroom.util

import com.zerocode.todoappciruitwithroom.di.appModule
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration

fun initKoinAndroid(appDeclaration: KoinAppDeclaration = {}) {
    startKoinCommon(appDeclaration)
}

fun initKoinIos() {
    startKoinCommon()
}

fun startKoinCommon(appDeclaration: KoinAppDeclaration = {}) {
    startKoin {
        appDeclaration()
        modules(appModule())
    }
}