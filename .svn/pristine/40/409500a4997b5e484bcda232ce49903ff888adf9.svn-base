package com.omni.support.ble.protocol.carport.model

import com.omni.support.ble.core.BufferDeserializable
import com.omni.support.ble.utils.BufferConverter

/**
 * @author 邱永恒
 *
 * @time 2019/8/23 14:51
 *
 * @desc
 *
 */
class CarportGetMacResult : BufferDeserializable {
    var isSuccess: Boolean = false
    var mac: String = ""
    var isAuto: Boolean = false

    override fun setBuffer(buffer: ByteArray) {
        val bc = BufferConverter(buffer)
        if (buffer.size == 7) {
            isSuccess = bc.u8 == 1
            val bytes = bc.getBytes(6)
            mac = getMacStr(bytes)
        } else if (buffer.size == 8) {
            isSuccess = bc.u8 == 1
            val bytes = bc.getBytes(6)
            mac = getMacStr(bytes)
            isAuto = bc.u8 == 2
        }
    }

    fun getMacStr(macAddress: ByteArray): String {
        return String.format(
            "%02X:%02X:%02X:%02X:%02X:%02X",
            macAddress[0],
            macAddress[1],
            macAddress[2],
            macAddress[3],
            macAddress[4],
            macAddress[5]
        )
    }

    override fun toString(): String {
        return "CarportGetMacResult(isSuccess=$isSuccess, mac='$mac', isAuto=$isAuto)"
    }
}