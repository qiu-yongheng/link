package com.omni.support.ble.protocol.carport.model

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
class CarportLockInfoResult : BufferDeserializable {
    var voltage: Int = 0
    var status1: Int = 0
    var status2: Int = 0
    var timestamp: Long = 0
    var mainVer: Int = 0
    var minorVer: Int = 0
    var numberOfEdits: Int = 0

    var isUnlock: Boolean = false
    var center: Boolean = false
    var reverseSide: Boolean = false
    var hasCar: Boolean = false
    var isAutoMode: Boolean = false
    var hasOldData: Boolean = false


    override fun setBuffer(buffer: ByteArray) {
        if (buffer.size != 7) {
            return
        }

        val bc = BufferConverter(buffer)
        voltage = bc.u16
        status1 = bc.u8
        status2 = bc.u8
        mainVer = bc.u8
        minorVer = bc.u8
        numberOfEdits = bc.u8

        isUnlock = status1 and 0x01 == 0x01
        center = status1 and 0x04 == 0x04
        reverseSide = status1 and 0x08 == 0x08
        hasCar = status1 and 0x10 == 0x10
        isAutoMode = status1 and 0x20 == 0x20
        hasOldData = status1 and 0x40 == 0x40
    }

    override fun toString(): String {
        return "CarportLockInfoResult(voltage=$voltage, status1=$status1, status2=$status2, timestamp=$timestamp, mainVer=$mainVer, minorVer=$minorVer, numberOfEdits=$numberOfEdits, isUnlock=$isUnlock, center=$center, reverseSide=$reverseSide, hasCar=$hasCar, isAutoMode=$isAutoMode, hasOldData=$hasOldData)"
    }
}