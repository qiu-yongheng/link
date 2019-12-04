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
class BSJEntryFingerprintResult : BufferDeserializable {
    var status: Int = 0
    var curCount: Int = 0
    var claimCount: Int = 0
    var fId: Int = 0


    override fun setBuffer(buffer: ByteArray) {
        if (buffer.size != 4) {
            return
        }

        val bc = BufferConverter(buffer)
        status = bc.u8
        curCount = bc.u8
        claimCount = bc.u8
        fId = bc.u8
    }

    override fun toString(): String {
        return "BSJEntryFingerprintResult(status=$status, curCount=$curCount, claimCount=$claimCount, fId=$fId)"
    }
}