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
class ULockOldDataResult : BufferDeserializable {
    var timestamp: Long = 0
    var useTime: Long = 0
    var uid: Long = 0

    override fun setBuffer(buffer: ByteArray) {
        if (buffer.size != 12) {
            return
        }
        val bc = BufferConverter(buffer)
        timestamp = bc.u32
        useTime = bc.u32
        uid = bc.u32
    }

    override fun toString(): String {
        return "ULockOldDataResult(timestamp=$timestamp, useTime=$useTime, uid=$uid)"
    }

}