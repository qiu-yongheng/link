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
class OldDataResult : BufferDeserializable {
    var timestamp: Long = 0
    // 分钟
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
        return "OldDataResult(timestamp=$timestamp, useTime=$useTime, uid=$uid)"
    }
}