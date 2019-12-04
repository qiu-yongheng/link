package com.omni.support.ble.protocol.keybox.model

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
class KeyboxLockInfoResult : BufferDeserializable {
    var voltage: Int = 0
    var status: Int = 0
    var timestamp: Long = 0
    var mainVer: Int = 0
    var minorVer: Int = 0
    var numberOfEdits: Int = 0

    var isUnlock: Boolean = false
    var hasOldData: Boolean = false
    var hasKey1: Boolean = false
    var hasKey2: Boolean = false
    var hasKey3: Boolean = false
    var isAlertOpen: Boolean = false
    var isPwdFull: Boolean = false


    override fun setBuffer(buffer: ByteArray) {
        if (buffer.size != 10) {
            return
        }

        val bc = BufferConverter(buffer)
        voltage = bc.u16
        status = bc.u8
        timestamp = bc.u32
        mainVer = bc.u8
        minorVer = bc.u8
        numberOfEdits = bc.u8

        isUnlock = status and 0x01 == 0x01
        hasOldData = status and 0x02 == 0x02
        hasKey1 = status and 0x04 == 0x04
        hasKey2 = status and 0x08 == 0x08
        hasKey3 = status and 0x10 == 0x10
        isAlertOpen = status and 0x20 == 0x20
        isPwdFull = status and 0x40 == 0x40
    }

    override fun toString(): String {
        return "KeyboxLockInfoResult(voltage=$voltage, status=$status, timestamp=$timestamp, mainVer=$mainVer, minorVer=$minorVer, numberOfEdits=$numberOfEdits, isUnlock=$isUnlock, hasOldData=$hasOldData, hasKey1=$hasKey1, hasKey2=$hasKey2, hasKey3=$hasKey3, isAlertOpen=$isAlertOpen, isPwdFull=$isPwdFull)"
    }
}