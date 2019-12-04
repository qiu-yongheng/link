package com.omni.support.ble.protocol.bloom

import com.omni.support.ble.core.IPack
import com.omni.support.ble.protocol.base.abde.ABDEPackAdapter

/**
 * @author 邱永恒
 *
 * @time 2019/8/29 16:27
 *
 * @desc
 *
 */
class BloomPackAdapter : ABDEPackAdapter() {
    override fun onNotify(buffer: ByteArray): IPack {
        return BloomPack.from(buffer)
    }
}