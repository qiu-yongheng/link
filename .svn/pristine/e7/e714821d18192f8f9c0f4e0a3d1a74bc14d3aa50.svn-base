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
class BLUnlockResult : BufferDeserializable {
    var isSuccess: Boolean = false

    override fun setBuffer(buffer: ByteArray) {
        if (buffer.size != 1) {
            return
        }
        val bc = BufferConverter(buffer)
        isSuccess = bc.u8 == 0
    }
}