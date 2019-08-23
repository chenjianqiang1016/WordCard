package com.demo.wordcard.util

import android.util.Log

object LogUtil {

    private val tag ="WordCard"

    fun errorTypeInfo(msg:String){
        Log.e(tag,msg)
    }

}