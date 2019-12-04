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
class KeyboxLockRecordResult : BufferDeserializable {
    var recordIndex: Int = 0
    var openKey: Long = 0
    var openTimestamp: Long = 0
    var openTime: Int = 0
    var keyStatus: Int = 0

    override fun setBuffer(buffer: ByteArray) {
        if (buffer.size != 13) {
            return
        }

        val bc = BufferConverter(buffer)
        recordIndex = bc.u16
        openKey = bc.u32
        openTimestamp = bc.u32
        openTime = bc.u16
        keyStatus = bc.u8
    }

    override fun toString(): String {
        return "KeyboxLockRecordResult(recordIndex=$recordIndex, openKey=$openKey, openTimestamp=$openTimestamp, openTime=$openTime, keyStatus=$keyStatus)"
    }
}