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
class HJUlockResult : BufferDeserializable {
    // 0开锁成功，01开锁失败，02开锁超时，03电量低不支持开锁，0xFF其他错误
    var status: Int = 0

    override fun setBuffer(buffer: ByteArray) {
        if (buffer.size != 1) {
            return
        }

        val bc = BufferConverter(buffer)
        status = bc.u8
    }

    override fun toString(): String {
        return "HJUlockResult(status=$status)"
    }

}