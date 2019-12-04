package com.omni.support.ble.protocol.bsj

import com.omni.support.ble.core.IPack
import com.omni.support.ble.utils.BufferConverter
import com.omni.support.ble.utils.HexString

/**
 * @author 邱永恒
 *
 * @time 2019/9/6 9:49
 *
 * @desc 应答
 *
 */
class BSJReplyPack : BSJPack() {
    override fun setBuffer(buffer: ByteArray) {
        val bc = BufferConverter(buffer)

        val prefix = bc.u8
        command = bc.u16
        val len = bc.u8

        debug("recv: cmd= ${HexString.valueOf(command)}, len= $len")

        if (len != 1) {
            debug("len error: $len")
            return
        }

        this.payload = bc.getBytes(len)
        debug("bsj reply command = ${HexString.valueOf(command)}, result = ${payload[0].toInt() == 0x00}")
    }

    companion object {
        fun from(buffer: ByteArray): IPack {
            val pack = BSJReplyPack()
            pack.setBuffer(buffer)
            return pack
        }
    }
}