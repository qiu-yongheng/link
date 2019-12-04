package com.omni.support.ble.protocol.ulock.model

import com.omni.support.ble.core.BufferDeserializable
import com.omni.support.ble.utils.BufferConverter

/**
 * @author 邱永恒
 *
 * @time 2019/8/8 15:55
 *
 * @desc
 *
 */
class ULockLockResult : BufferDeserializable {
    var isSuccess: Boolean = false
    var timestamp: Long = 0
    var ridingTime: Long = 0

    override fun setBuffer(buffer: ByteArray) {
        if (buffer.size != 9) {
            return
        }
        val bc = BufferConverter(buffer)
        isSuccess = bc.u8 == 0
        timestamp = bc.u32
        ridingTime = bc.u32
    }

    override fun toString(): String {
        return "ULockLockResult(isUnlock=$isSuccess, timestamp=$timestamp, ridingTime=$ridingTime)"
    }
}