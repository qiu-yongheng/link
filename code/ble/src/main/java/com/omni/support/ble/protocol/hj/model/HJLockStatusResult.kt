package com.omni.support.ble.protocol.hj.model

import com.omni.support.ble.core.BufferDeserializable
import com.omni.support.ble.utils.BufferConverter
import kotlin.math.min

/**
 * @author 邱永恒
 *
 * @time 2019/8/23 14:51
 *
 * @desc
 *
 */
class HJLockStatusResult : BufferDeserializable {
    var isUnlok: Boolean = false

    override fun setBuffer(buffer: ByteArray) {
        if (buffer.size != 1) {
            return
        }

        val bc = BufferConverter(buffer)
        isUnlok = bc.u8 == 1
    }

    override fun toString(): String {
        return "HJLockStatusResult(isUnlok=$isUnlok)"
    }


}