package com.omni.support.ble.protocol.ulock

import android.util.Log
import com.omni.support.ble.core.IPack
import com.omni.support.ble.core.IPackResolver
import com.omni.support.ble.link.LinkGlobalSetting
import com.omni.support.ble.protocol.base.EmptyPack
import com.omni.support.ble.protocol.base.LogPack
import com.omni.support.ble.protocol.base.ReadPack
import com.omni.support.ble.protocol.base.fe.FEPackAdapter
import com.omni.support.ble.protocol.bike.BLPack
import com.omni.support.ble.utils.BufferConverter
import com.omni.support.ble.utils.CRC

/**
 * @author 邱永恒
 *
 * @time 2019/8/30 13:38
 *
 * @desc
 *
 */
class ULockPackAdapter: FEPackAdapter() {
    override fun onNotify(buffer: ByteArray): IPack {
        return ULockPack.from(buffer)
    }

}