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
class HJSetWorkModeResult : BufferDeserializable {
    var isSuccess: Boolean = false

    override fun setBuffer(buffer: ByteArray) {
        if (buffer.size != 1) {
            return
        }

        val bc = BufferConverter(buffer)
        isSuccess = bc.u8 == 0
    }

    override fun toString(): String {
        return "HJSetWorkModeResult(isSuccess=$isSuccess)"
    }
}