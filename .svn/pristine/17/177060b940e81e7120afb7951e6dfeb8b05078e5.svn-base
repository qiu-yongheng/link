package com.omni.support.ble.protocol.base.model

import com.omni.support.ble.core.BufferDeserializable
import com.omni.support.ble.utils.BufferConverter
import com.omni.support.ble.utils.HexString

/**
 * @author 邱永恒
 *
 * @time 2019/8/8 15:55
 *
 * @desc
 *
 */
class ReadResult : BufferDeserializable {
    var pack: Int = 0
    var data: ByteArray = ByteArray(0)

    override fun setBuffer(buffer: ByteArray) {
        if (buffer.size != 18) {
            return
        }
        val bc = BufferConverter(buffer)
        pack = bc.u16
        data = bc.getBytes(16)
    }

    override fun toString(): String {
        return "ReadResult(pack=$pack, data=${HexString.valueOf(data)})"
    }


}