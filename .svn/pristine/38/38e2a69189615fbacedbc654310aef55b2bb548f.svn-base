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
class KeyboxLockResult : BufferDeserializable {
    var isSuccess: Boolean = false
    var keyStatus: Int = 0
    var isUse1: Boolean = false
    var isUse2: Boolean = false
    var isUse3: Boolean = false
    var timestamp: Long = 0

    override fun setBuffer(buffer: ByteArray) {
        if (buffer.size != 6) {
            return
        }

        val bc = BufferConverter(buffer)
        isSuccess = bc.u8 == 1
        keyStatus = bc.u8
        timestamp = bc.u32

        isUse1 = keyStatus and 0x01 == 0x01
        isUse1 = keyStatus and 0x02 == 0x02
        isUse1 = keyStatus and 0x04 == 0x04
    }

    override fun toString(): String {
        return "KeyboxLockResult(isSuccess=$isSuccess, keyStatus=$keyStatus, isUse1=$isUse1, isUse2=$isUse2, isUse3=$isUse3, timestamp=$timestamp)"
    }
}