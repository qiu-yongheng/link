package com.omni.support.ble.protocol.scooter

import com.omni.support.ble.core.IPack
import com.omni.support.ble.protocol.base.a3a4.A3A4PackAdapter

/**
 * @author 邱永恒
 *
 * @time 2019/8/23 17:33
 *
 * @desc
 *
 */
class ScooterPackAdapter : A3A4PackAdapter() {
    override fun onNotify(buffer: ByteArray): IPack {
        return ScooterPack.from(buffer)
    }
}