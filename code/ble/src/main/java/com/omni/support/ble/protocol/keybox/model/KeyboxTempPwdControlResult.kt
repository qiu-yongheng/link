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
class KeyboxTempPwdControlResult : BufferDeserializable {
    var status: Int = 0
    var totalPwdNum: Int = 0
    var optType: Int = 0

    override fun setBuffer(buffer: ByteArray) {
        if (buffer.size != 3) {
            return
        }

        val bc = BufferConverter(buffer)
        status = bc.u8
        totalPwdNum = bc.u8
        optType = bc.u8
    }

    override fun toString(): String {
        return "KeyboxTempPwdControlResult(status=$status, totalPwdNum=$totalPwdNum, optType=$optType)"
    }
}