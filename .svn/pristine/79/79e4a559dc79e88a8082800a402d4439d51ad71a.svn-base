package com.omni.support.ble.protocol.base

import com.omni.support.ble.core.IPack

/**
 * @author 邱永恒
 *
 * @time 2019/8/8 11:56
 *
 * @desc 当协议帧不合法时, 发送该pack
 *
 */
class EmptyPack: IPack {
    override fun getBuffer(): ByteArray {
        return ByteArray(0)
    }

    override fun setBuffer(buffer: ByteArray) {
    }
}