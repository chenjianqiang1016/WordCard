package com.demo.wordcard

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

val SP_NAME = "WordCard"

/**
 * SharedPreferences工具类
 */
object SpUtil {

    private val context = MyApplication.instance()
    fun put(key: String, value: Any) {
        context.put(key, value)
    }

    fun getBooleanValue(key: String,default: Boolean = false): Boolean = context.getBooleanValue(key,default)

    fun getStringValue(key: String,default:String? = ""): String? = context.getStringValue(key,default)

}

/**
 * 存储sp信息，int .long.float.string.boolean
 */
fun Context.put(key: String, value: Any) {
    getSharePreference().edit {
        when (value) {
            is Int -> putInt(key, value)
            is String -> putString(key, value)
            is Float -> putFloat(key, value)
            is Long -> putLong(key, value)
            is Boolean -> putBoolean(key, value)
        }
    }
}

/**
 * 获取sp中key对应布尔值
 */
fun Context.getBooleanValue(key: String, default: Boolean = false): Boolean =
    getSharePreference().getBoolean(key, default)

/**
 * 获取sp中key对应字符串
 */
fun Context.getStringValue(key: String, default: String? = ""): String? = getSharePreference().getString(key, default)



fun Context.getSharePreference(): SharedPreferences {
    return this.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE)
}