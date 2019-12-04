package com.omni.support.ble.protocol.bsj

import android.util.Log
import com.omni.support.ble.core.IPack
import com.omni.support.ble.link.LinkGlobalSetting
import com.omni.support.ble.utils.AESUtils
import com.omni.support.ble.utils.BufferConverter
import com.omni.support.ble.utils.HexString
import java.util.*

/**
 * @author 邱永恒
 *
 * @time 2019/9/6 9:37
 *
 * @desc 博实结
 *
 */
open class BSJPack : IPack {
    var payload: ByteArray = ByteArray(0)
    var command: Int = 0
    var token: Long = 0
    var aesKey: ByteArray = ByteArray(0)

    override fun getBuffer(): ByteArray {
        val payloadSize = payload.size
        val reservedSize = 16 - 7 - payloadSize
        val bc = BufferConverter(16)

        // command
        bc.putU16(command)
        // len
        bc.putU8(payloadSize)
        // payload
        bc.putBytes(payload)
        // token
        bc.putU32(token)

        if (reservedSize > 0) {
            for (i in 0 until reservedSize) {
                bc.putU8(Random().nextInt(255))
            }
        }

        val command = bc.buffer()
        debug("send origin " + command.size + " bytes: " + HexString.valueOf(command))

        // AES-128加密
        return AESUtils.encrypt(command, aesKey)
    }

    override fun setBuffer(buffer: ByteArray) {
    }

    fun debug(message: String) {
        if (LinkGlobalSetting.DEBUG) {
            Log.d(this.javaClass.simpleName, message)
        }
    }

    companion object {
        const val PREFIX = 0xBB
    }
}