package com.omni.support.ble.protocol.keybox.model

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
class KeyboxKeyStatusResult : BufferDeserializable {
    var keyStatus: Int = 0

    override fun setBuffer(buffer: ByteArray) {
        if (buffer.size != 1) {
            return
        }

        val bc = BufferConverter(buffer)
        keyStatus = bc.u8
    }

    override fun toString(): String {
        return "KeyboxKeyStatusResult(keyStatus=$keyStatus)"
    }
}