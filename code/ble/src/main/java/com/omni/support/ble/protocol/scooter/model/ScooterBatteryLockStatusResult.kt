package com.omni.support.ble.protocol.scooter.model

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
class ScooterBatteryLockStatusResult : BufferDeserializable {
    var operation: Int = 0
    var status: Int = 0

    override fun setBuffer(buffer: ByteArray) {
        if (buffer.size != 2) {
            return
        }

        val bc = BufferConverter(buffer)
        operation = bc.u8
        // 0x02->通信超时   0x10->关锁状态  0x11->开锁状态
        status = bc.u8
    }

    override fun toString(): String {
        return "ScooterBatteryLockStatusResult(operation=$operation, status=$status)"
    }
}