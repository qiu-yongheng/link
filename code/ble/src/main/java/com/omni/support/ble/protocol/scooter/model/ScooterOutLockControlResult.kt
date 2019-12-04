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
class ScooterOutLockControlResult : BufferDeserializable {
    var operation: Int = 0
    var isSuccess: Boolean = false

    override fun setBuffer(buffer: ByteArray) {
        if (buffer.size != 2) {
            return
        }

        val bc = BufferConverter(buffer)
        operation = bc.u8
        isSuccess = bc.u8 == 1
    }

    override fun toString(): String {
        return "ScooterOutLockControlResult(operation=$operation, isSuccess=$isSuccess)"
    }
}