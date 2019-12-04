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
class BSJBatteryStatusResult : BufferDeserializable {
    var isNeedCharging: Boolean = false


    override fun setBuffer(buffer: ByteArray) {
        if (buffer.size != 1) {
            return
        }

        val bc = BufferConverter(buffer)
        isNeedCharging = bc.u8 == 0x01
    }

    override fun toString(): String {
        return "BSJBatteryStatusResult(isNeedCharging=$isNeedCharging)"
    }
}