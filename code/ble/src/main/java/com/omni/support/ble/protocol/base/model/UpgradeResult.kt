package com.omni.support.ble.protocol.base.model

import com.omni.support.ble.core.BufferDeserializable
import com.omni.support.ble.utils.BufferConverter

/**
 * @author 邱永恒
 *
 * @time 2019/8/22 15:31
 *
 * @desc
 *
 */
class UpgradeResult : BufferDeserializable {
    var pack: Int = 0
    var deviceType: Int = 0

    override fun setBuffer(buffer: ByteArray) {
        if (buffer.size != 3) {
            return
        }
        val bc = BufferConverter(buffer)
        pack = bc.u16
        deviceType = bc.u8
    }

    override fun toString(): String {
        return "UpgradeResult(pack=$pack, deviceType=$deviceType)"
    }
}