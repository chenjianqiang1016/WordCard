package com.demo.wordcard.util

import android.text.TextUtils

class StringCheckUtil {


    companion object {


        /**
         * 检查传入的字符串，是否为 null、""、"null"、"Null"、"NULL"等
         *
         * return true，表示传入字符串为上述的空情况
         */
        fun checkStringIsNull(str: String): Boolean {

            if (str.isEmpty()) {
                return true
            }

            return TextUtils.equals("null".toUpperCase(), str.toUpperCase())

        }

    }

}