package com.omni.support.ble.protocol.hj

import android.util.Log
import com.omni.support.ble.core.IPack
import com.omni.support.ble.link.LinkGlobalSetting
import com.omni.support.ble.utils.AESUtils
import com.omni.support.ble.utils.BufferConverter
import com.omni.support.ble.utils.HexString

/**
 * @author 邱永恒
 *
 * @time 2019/9/24 11:59
 *
 * @desc
 *
 */
class HJPack: IPack {
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

//        if (reservedSize > 0) {
//            for (i in 0 until reservedSize) {
//                bc.putU8(Random().nextInt(255))
//            }
//        }

        val command = bc.buffer()
        debug("send origin " + command.size + " bytes: " + HexString.valueOf(command))

        // AES-128加密
        return AESUtils.encrypt(command, aesKey)
    }

    override fun setBuffer(buffer: ByteArray) {
        val bc = BufferConverter(buffer)

        command = bc.u16
        val len = bc.u8

        debug("recv: cmd= ${HexString.valueOf(command)}, len= $len")

        if (len < 0) {
            debug("len error: $len")
            return
        }

        this.payload = bc.getBytes(len)
    }

    fun debug(message: String) {
        if (LinkGlobalSetting.DEBUG) {
            Log.d(this.javaClass.simpleName, message)
        }
    }

    companion object {
        fun from(buffer: ByteArray): IPack {
            val pack = HJPack()
            pack.setBuffer(buffer)
            return pack
        }
    }
}