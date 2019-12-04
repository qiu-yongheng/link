package com.omni.support.ble.protocol.base.model

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
class UnlockResult : BufferDeserializable {
    var isSuccess: Boolean = false
    var timestamp: Long = 0

    override fun setBuffer(buffer: ByteArray) {
        if (buffer.size != 5) {
            return
        }

        val bc = BufferConverter(buffer)
        isSuccess = bc.u8 == 0
        timestamp = bc.u32
    }

    override fun toString(): String {
        return "UnlockResult(isSuccess=$isSuccess, timestamp=$timestamp)"
    }
}