package com.omni.support.ble.utils

import android.util.Log

/**
 * @author 邱永恒
 *
 * @time 2019/9/10 10:21
 *
 * @desc
 *
 */
object BSJUtils {
    fun toByteArray(data: String): ByteArray {
//        var number = data.toInt()
//        val byteArray = ByteArray(data.length)
//        var index = data.length - 1
//        while (number > 0) {
//            byteArray[index--] = (number % 10).toByte()
//            number /= 10
//        }

        val byteArray = data.toByteArray()

        Log.d("=====", HexString.valueOf(byteArray))
        return byteArray
    }
}