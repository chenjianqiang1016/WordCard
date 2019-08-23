package com.demo.wordcard

import android.app.Application
import kotlin.properties.Delegates

class MyApplication : Application() {

    companion object {

        private var instance: MyApplication by Delegates.notNull()

        fun instance() = instance
    }

    override fun onCreate() {
        super.onCreate()

        instance = this
    }


}