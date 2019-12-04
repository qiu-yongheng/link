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
class CarportSetModeResult : BufferDeserializable {
    var mode: Int = 0

    override fun setBuffer(buffer: ByteArray) {
        if (buffer.size != 1) {
            return
        }

        val bc = BufferConverter(buffer)
        mode = bc.u8
    }

    override fun toString(): String {
        return "CarportSetModeResult(mode=$mode)"
    }
}