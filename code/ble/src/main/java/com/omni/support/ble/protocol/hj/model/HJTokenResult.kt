package com.omni.support.ble.protocol.hj.model

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
class HJTokenResult : BufferDeserializable {
    var token: Long = 0
    var workModel: Int = 0
    var mainVer: Int = 0
    var minorVer: Int = 0

    override fun setBuffer(buffer: ByteArray) {
        if (buffer.size != 7) {
            return
        }

        val bc = BufferConverter(buffer)
        token = bc.u32
        workModel = bc.u8
        mainVer = bc.u8
        minorVer = bc.u8
    }

    override fun toString(): String {
        return "HJTokenResult(token=$token, workModel=$workModel, mainVer=$mainVer, minorVer=$minorVer)"
    }
}