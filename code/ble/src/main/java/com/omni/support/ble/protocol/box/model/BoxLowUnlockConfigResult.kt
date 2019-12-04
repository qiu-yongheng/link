package com.omni.support.ble.protocol.box.model

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
class BoxLowUnlockConfigResult : BufferDeserializable {
    var isLowUnlock: Boolean = false

    override fun setBuffer(buffer: ByteArray) {
        if (buffer.size != 1) {
            return
        }

        val bc = BufferConverter(buffer)
        isLowUnlock = bc.u8 == 1
    }

    override fun toString(): String {
        return "BoxLowUnlockConfigResult(isLowUnlock=$isLowUnlock)"
    }
}