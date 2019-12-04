package com.omni.support.ble.protocol.base.model

import com.omni.support.ble.core.BufferDeserializable
import com.omni.support.ble.utils.BufferConverter
import com.omni.support.ble.utils.HexString

/**
 * @author 邱永恒
 *
 * @time 2019/8/8 15:55
 *
 * @desc
 *
 */
class StartReadResult : BufferDeserializable {
    var totalPack: Int = 0
    var crc: Int = 0
    var deviceType: Int = 0

    override fun setBuffer(buffer: ByteArray) {
        if (buffer.size != 9) {
            return
        }
        val bc = BufferConverter(buffer)
        totalPack = bc.u16
        crc = bc.u16
        deviceType = bc.u8
    }

    override fun toString(): String {
        return "StartReadResult(totalPack=$totalPack, crc=${HexString.valueOf(crc)}, deviceType=${HexString.valueOf(deviceType)})"
    }
}