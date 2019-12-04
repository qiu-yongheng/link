package com.omni.support.ble.protocol

import android.util.Log
import com.omni.support.ble.core.IPack
import com.omni.support.ble.link.LinkGlobalSetting
import kotlin.experimental.xor

/**
 * @author 邱永恒
 *
 * @time 2019/8/7 14:32
 *
 * @desc 协议包基类
 *
 */
abstract class Pack: IPack {
    var payload: ByteArray = ByteArray(0)
    var key: Int = 0
    var cmd: Int = 0

    /**
     * 加密
     * 马蹄锁
     */
    fun bikeDecode(command: ByteArray): ByteArray {
        val xorComm = ByteArray(command.size)
        xorComm[0] = command[0]
        xorComm[1] = (command[1] + 0x32).toByte()
        for (i in 2 until command.size) {
            xorComm[i] = (command[i] xor command[1])
        }
        return xorComm
    }

    /**
     * 加密
     * 滑板车
     */
    fun scooterDecode(command: ByteArray): ByteArray {
        val xorComm = ByteArray(command.size)
        xorComm[0] = command[0]
        xorComm[1] = command[1]
        xorComm[2] = command[2]
        xorComm[3] = (command[3] + 0x32).toByte()
        for (i in 4 until command.size) {
            xorComm[i] = (command[i] xor command[3])
        }
        return xorComm
    }



    fun debug(message: String) {
        if (LinkGlobalSetting.DEBUG) {
            Log.d(this.javaClass.simpleName, message)
        }
    }
}