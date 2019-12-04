package com.omni.support.ble.protocol.bsj.model

import com.omni.support.ble.core.BufferDeserializable
import com.omni.support.ble.utils.BufferConverter
import com.omni.support.ble.utils.HexString
import kotlin.math.min

/**
 * @author 邱永恒
 *
 * @time 2019/8/23 14:51
 *
 * @desc
 *
 */
class BSJFingerLockStatusResult : BufferDeserializable {
    var totalPack: Int = 0
    var curPack: Int = 0
    var payload: ByteArray = ByteArray(0)


    override fun setBuffer(buffer: ByteArray) {
        if (buffer.size != 12) {
            return
        }

        val bc = BufferConverter(buffer)
        totalPack = bc.u8
        curPack = bc.u8
        payload = bc.getBytes(10)
    }

    override fun toString(): String {
        return "BSJFingerLockStatusResult(totalPack=$totalPack, curPack=$curPack, payload=${HexString.valueOf(payload)})"
    }
}