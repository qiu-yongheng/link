package com.omni.support.ble.protocol.scooter.model

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
class ScooterLockInfoResult : BufferDeserializable {
    var voltage: Int = 0
    var status: Int = 0
    var isUnlock: Boolean = false
    var hasOldData: Boolean = false
    var mainVersion: Int = 0
    var minorVersion: Int = 0
    var numberOfEdits: Int = 0

    override fun setBuffer(buffer: ByteArray) {
        if (buffer.size != 7) {
            return
        }

        val bc = BufferConverter(buffer)
        voltage = bc.u16
        status = bc.u8
        isUnlock = status or 0x01 == 1
        hasOldData = status or 0x40 == 1
        bc.offset(1)
        mainVersion = bc.u8
        minorVersion = bc.u8
        numberOfEdits = bc.u8
    }

    override fun toString(): String {
        return "ScooterLockInfoResult(voltage=$voltage, status=$status, isUnlock=$isUnlock, hasOldData=$hasOldData, mainVersion=$mainVersion, minorVersion=$minorVersion, numberOfEdits=$numberOfEdits)"
    }
}