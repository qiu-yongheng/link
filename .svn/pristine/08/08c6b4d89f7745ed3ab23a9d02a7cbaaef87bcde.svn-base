package com.omni.support.ble.protocol.bsj.model

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
class BSJDeleteFingerResult : BufferDeserializable {
    var isSuccess: Boolean = false
    var remainAmount: Int = 0


    override fun setBuffer(buffer: ByteArray) {
        if (buffer.size != 2) {
            return
        }

        val bc = BufferConverter(buffer)
        isSuccess = bc.u8 == 0x00
        remainAmount = bc.u8
    }

    override fun toString(): String {
        return "BSJDeleteFingerResult(isSuccess=$isSuccess, remainAmount=$remainAmount)"
    }
}