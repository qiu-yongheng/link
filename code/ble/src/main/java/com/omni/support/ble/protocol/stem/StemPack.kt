package com.omni.support.ble.protocol.stem

import com.omni.support.ble.core.IPack
import com.omni.support.ble.protocol.Pack
import com.omni.support.ble.utils.BufferConverter
import com.omni.support.ble.utils.HexString

/**
 * @author 邱永恒
 *
 * @time 2019/11/11 15:17
 *
 * @desc
 *
 */
class StemPack : Pack() {
    override fun getBuffer(): ByteArray {
        val payloadSize = payload.size
        val frameSize = 1 + payloadSize
        val bc = BufferConverter(frameSize)

        // 协议帧
        bc.putU8(cmd)
        // payload
        bc.putBytes(payload)

//        debug("send origin " + bc.buffer().size + " bytes: " + HexString.valueOf(bc.buffer()))
        return bc.buffer()
    }

    override fun setBuffer(buffer: ByteArray) {
        payload = buffer
    }

    companion object {
        const val PREFIX_CONFIG = 0xF1
        const val PREFIX_CONTROL = 0xF2
        const val PREFIX_CYCLING = 0xF3
        const val PREFIX_SENSOR = 0xF4
        const val PREFIX_COUNT = 0xF6
        const val PREFIX_SERIAL = 0xF5

        fun from(prefix: Int, buffer: ByteArray): IPack {
            val pack = StemPack()
            pack.cmd = prefix
            pack.setBuffer(buffer)
            return pack
        }
    }

}