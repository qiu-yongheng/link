package com.omni.support.ble.protocol.carport.model

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
class CarportStartReadResult : BufferDeserializable {
    var type: Int = 0
    var totalPack: Int = 0
    var crc: Int = 0
    var deviceType: Int = 0
    var vifKey: Long = 0

    override fun setBuffer(buffer: ByteArray) {
        if (buffer.size != 10) {
            return
        }
        val bc = BufferConverter(buffer)
        type = bc.u8
        totalPack = bc.u16
        crc = bc.u16
        deviceType = bc.u8
        vifKey = bc.u32
    }

    override fun toString(): String {
        return "CarportStartReadResult(type=$type, totalPack=$totalPack, crc=$crc, deviceType=$deviceType, vifKey=$vifKey)"
    }
}