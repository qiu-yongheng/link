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
class ScooterKeyResult : BufferDeserializable {
    var isSuccess: Boolean = false
    var key: Int = 0

    override fun setBuffer(buffer: ByteArray) {
        if (buffer.size != 2) {
            return
        }

        val bc = BufferConverter(buffer)
        isSuccess = bc.u8 == 1
        key = bc.u8
    }

    override fun toString(): String {
        return "ScooterKeyResult(isSuccess=$isSuccess, key=$key)"
    }
}