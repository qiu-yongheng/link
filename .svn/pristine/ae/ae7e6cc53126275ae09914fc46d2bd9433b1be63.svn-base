package com.omni.support.ble.protocol.ulock.model

import com.omni.support.ble.core.BufferDeserializable
import com.omni.support.ble.utils.BufferConverter

/**
 * @author 邱永恒
 *
 * @time 2019/8/7 17:19
 *
 * @desc
 *
 */
class ULockKeyResult : BufferDeserializable {
    var key: Int = 0

    override fun setBuffer(buffer: ByteArray) {
        if (buffer.size != 1) {
            return
        }
        val bc = BufferConverter(buffer)
        key = bc.u8
    }

    override fun toString(): String {
        return "ULockKeyResult(key=$key)"
    }
}