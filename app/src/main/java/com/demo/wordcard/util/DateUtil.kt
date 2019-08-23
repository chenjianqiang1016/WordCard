package com.demo.wordcard.util

import java.util.*

/**
 * 时间工具
 */
object DateUtil {


    /**
     * 获取当天的0时0分0秒时间戳
     * 如：现在是：2019年07月18日16时54分24秒
     *
     * 返回结果为：2019年07月18日00时00分00秒 对应的毫秒值
     */
    fun getCurrentDayZeroTime(): Long {
        val current = System.currentTimeMillis()//当前时间毫秒数
        val zeroTime =
            current / (1000 * 3600 * 24) * (1000 * 3600 * 24) - TimeZone.getDefault().getRawOffset()//今天零点零分零秒的毫秒数

        return zeroTime

    }

    /**
     * 获取当天的23时59分59秒时间戳
     * 如：现在是：2019年07月18日16时54分24秒
     *
     * 返回结果为：2019年07月18日23时59分59秒 对应的毫秒值
     */
    fun getCurrentDayFinishTime(): Long {

        val current = System.currentTimeMillis()//当前时间毫秒数
        val zero =
            current / (1000 * 3600 * 24) * (1000 * 3600 * 24) - TimeZone.getDefault().getRawOffset()//今天零点零分零秒的毫秒数
        val finshTime = zero + 24 * 60 * 60 * 1000 - 1//今天23点59分59秒的毫秒数

        return finshTime

    }


}