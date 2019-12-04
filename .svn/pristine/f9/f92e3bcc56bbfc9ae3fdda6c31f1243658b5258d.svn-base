package com.omni.support.ble.protocol.bsj

import com.omni.support.ble.core.IPack
import com.omni.support.ble.utils.BufferConverter
import com.omni.support.ble.utils.HexString

/**
 * @author 邱永恒
 *
 * @time 2019/9/6 9:59
 *
 * @desc 响应
 *
 */
class BSJResultPack : BSJPack() {
    override fun setBuffer(buffer: ByteArray) {
        val bc = BufferConverter(buffer)

        command = bc.u16
        val len = bc.u8

        debug("recv: cmd= ${HexString.valueOf(command)}, len= $len")

        if (len < 0) {
            debug("len error: $len")
            return
        }

        this.payload = bc.getBytes(len)
    }

    companion object {
        fun from(buffer: ByteArray): IPack {
            val pack = BSJResultPack()
            pack.setBuffer(buffer)
            return pack
        }
    }
}