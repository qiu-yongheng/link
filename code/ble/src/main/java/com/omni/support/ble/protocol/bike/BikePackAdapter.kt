package com.omni.support.ble.protocol.bike

import com.omni.support.ble.core.IPack
import com.omni.support.ble.protocol.base.fe.FEPackAdapter

/**
 * @author 邱永恒
 *
 * @time 2019/8/7 16:16
 *
 * @desc 马蹄锁包解析器
 *
 */
class BikePackAdapter : FEPackAdapter() {
    override fun onNotify(buffer: ByteArray): IPack {
        return BLPack.from(buffer)
    }
}