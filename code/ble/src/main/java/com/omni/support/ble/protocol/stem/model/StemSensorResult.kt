package com.omni.support.ble.protocol.stem.model

import com.omni.support.ble.core.BufferDeserializable
import com.omni.support.ble.utils.BufferConverter

/**
 * @author 邱永恒
 *
 * @time 2019/11/12 16:03
 *
 * @desc
 *
 */
class StemSensorResult: BufferDeserializable {
    // 坡度
    var slope: Int = 0
    // 温度
    var temp: Int = 0
    // 高度
    var alt: Int = 0
    // 气压
    var bar: Int = 0
    // 电量百分比
    var powerPercent: Int = 0
    // 充电状态
    var powerState: Int = 0

    companion object {
        // 放电状态
        const val STATE_DISCHARGE = 0
        // 充电状态
        const val STATE_CHARGING = 1
    }


    override fun setBuffer(buffer: ByteArray) {
        if (buffer.size != 20) {
            return
        }

        val bc = BufferConverter(buffer)
        slope = bc.u8
        temp = bc.u16
        alt = bc.u16
        bar = bc.u16
        powerPercent = bc.u8
        powerState = bc.u8
    }

    override fun toString(): String {
        return "StemSensorResult(slope=$slope, temp=$temp, alt=$alt, bar=$bar, powerPercent=$powerPercent, powerState=$powerState)"
    }
}