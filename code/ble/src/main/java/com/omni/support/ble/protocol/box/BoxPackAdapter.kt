package com.omni.support.ble.protocol.box

import com.omni.support.ble.core.IPack
import com.omni.support.ble.protocol.base.a3a4.A3A4PackAdapter

/**
 * @author 邱永恒
 *
 * @time 2019/9/2 15:34
 *
 * @desc
 *
 */
class BoxPackAdapter: A3A4PackAdapter() {
    override fun onNotify(buffer: ByteArray): IPack {
        return BoxPack.from(buffer)
    }
}