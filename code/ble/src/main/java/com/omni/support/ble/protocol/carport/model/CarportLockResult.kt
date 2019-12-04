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
class CarportLockResult : BufferDeserializable {
    var isSuccess: Boolean = false
    var isStart: Boolean = false
    var isTimeout: Boolean = false
    var timestamp: Long = 0
    // 秒
    var useTime: Long = 0

    override fun setBuffer(buffer: ByteArray) {
        if (buffer.size != 9) {
            return
        }

        val bc = BufferConverter(buffer)
        val status = bc.u8
        isStart = status == 0
        isSuccess = status == 1
        isTimeout = status == 2
        timestamp = bc.u32
        useTime = bc.u32
    }

    override fun toString(): String {
        return "CarportLockResult(isSuccess=$isSuccess, isStart=$isStart, isTimeout=$isTimeout, timestamp=$timestamp, useTime=$useTime)"
    }
}