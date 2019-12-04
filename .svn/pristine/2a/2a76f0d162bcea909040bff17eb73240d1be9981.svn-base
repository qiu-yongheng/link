package com.omni.support.ble.protocol.bike.model

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
class BLInfoResult : BufferDeserializable {
    var isUnlock: Boolean = false
    var voltage: Int = 0
    var hasOldData: Boolean = false
    var timestamp: Long = 0

    override fun setBuffer(buffer: ByteArray) {
        if (buffer.size != 7) {
            return
        }
        val bc = BufferConverter(buffer)
        isUnlock = bc.u8 == 0
        voltage = bc.u8
        hasOldData = bc.u8 == 0
        timestamp = bc.u32
    }

    override fun toString(): String {
        return "BLInfoResult(isUnlock=$isUnlock, voltage=$voltage, hasOldData=$hasOldData, timestamp=$timestamp)"
    }

}