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
class HJWorkModeResult : BufferDeserializable {
    // 0正常模式；1睡眠模式
    var status: Int = 0

    override fun setBuffer(buffer: ByteArray) {
        if (buffer.size != 1) {
            return
        }

        val bc = BufferConverter(buffer)
        status = bc.u8
    }

    override fun toString(): String {
        return "HJWorkModeResult(status=$status)"
    }


}