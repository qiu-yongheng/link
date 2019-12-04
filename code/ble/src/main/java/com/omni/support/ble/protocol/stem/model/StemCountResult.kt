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
class StemCountResult: BufferDeserializable {
    // 索引
    var index: Int = 0
    // 当前分钟
    var date: String = ""
    // 当前分钟运转圈数
    var count: Int = 0
    // 当前分钟踏频
    var cyclingCount: Int = 0

    override fun setBuffer(buffer: ByteArray) {
        if (buffer.size != 20) {
            return
        }

        val bc = BufferConverter(buffer)
        index = bc.u16
        date = String.format("%02d月%02d日%02d时%02d分", bc.u8, bc.u8, bc.u8, bc.u8)
        count = bc.u16
        cyclingCount = bc.u8
    }

    override fun toString(): String {
        return "StemCountResult(index=$index, date='$date', count=$count, cyclingCount=$cyclingCount)"
    }
}