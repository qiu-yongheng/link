package com.omni.support.ble.protocol.bsj.model

import com.omni.support.ble.core.BufferDeserializable
import com.omni.support.ble.utils.BufferConverter
import kotlin.math.min

/**
 * @author 邱永恒
 *
 * @time 2019/8/23 14:51
 *
 * @desc
 *
 */
class BSJTokenResult : BufferDeserializable {
    var token: Long = 0
    var chipType: Int = 0
    var mainVer: Int = 0
    var minorVer: Int = 0
    var deviceType: Int = 0


    override fun setBuffer(buffer: ByteArray) {
        if (buffer.size != 8) {
            return
        }

        val bc = BufferConverter(buffer)
        token = bc.u32
        chipType = bc.u8
        mainVer = bc.u8
        minorVer = bc.u8
        deviceType = bc.u8
    }

    override fun toString(): String {
        return "BSJTokenResult(token=$token, chipType=$chipType, mainVer=$mainVer, minorVer=$minorVer, deviceType=$deviceType)"
    }
}