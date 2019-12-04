package com.omni.support.ble.protocol.base

import com.omni.support.ble.core.IPack
import com.omni.support.ble.protocol.Pack

/**
 * @author 邱永恒
 *
 * @time 2019/8/7 14:36
 *
 * @desc 读
 *
 */
class LogPack : Pack() {

    override fun getBuffer(): ByteArray {
        return ByteArray(0)
    }

    override fun setBuffer(buffer: ByteArray) {
       this.payload = buffer
    }

    companion object {
        const val LOG = 0xC7

        fun from(cmd: Int, buffer: ByteArray): IPack {
            val pack = LogPack()
            pack.cmd = cmd
            pack.setBuffer(buffer)
            return pack
        }
    }
}