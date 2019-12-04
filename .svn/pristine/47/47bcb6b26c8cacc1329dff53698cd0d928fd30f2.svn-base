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
class ScooterInfoResult : BufferDeserializable {
    var power: Int = 0
    var speedMode: Int = 0
    var speed: Int = 0
    var singleRidingMileage: Int = 0
    var remainingMileage: Int = 0

    override fun setBuffer(buffer: ByteArray) {
        if (buffer.size != 8) {
            return
        }

        val bc = BufferConverter(buffer)
        power = bc.u8
        speedMode = bc.u8
        speed = bc.u16
        singleRidingMileage = bc.u16
        remainingMileage = bc.u16
    }

    override fun toString(): String {
        return "ScooterInfoResult(power=$power, speedMode=$speedMode, speed=$speed, singleRidingMileage=$singleRidingMileage, remainingMileage=$remainingMileage)"
    }
}