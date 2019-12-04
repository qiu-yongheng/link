package com.omni.support.ble.protocol.scooter.model

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
class ScooterRGBTestResult : BufferDeserializable {
    var operation: Int = 0

    override fun setBuffer(buffer: ByteArray) {
        if (buffer.size != 2) {
            return
        }

        val bc = BufferConverter(buffer)
        operation = bc.u8
    }

    override fun toString(): String {
        return "ScooterRGBTestResult(operation=$operation)"
    }
}