package com.omni.support.ble.protocol.keybox

import android.util.Log
import com.omni.support.ble.core.IPack
import com.omni.support.ble.core.IPackResolver
import com.omni.support.ble.link.LinkGlobalSetting
import com.omni.support.ble.protocol.base.EmptyPack
import com.omni.support.ble.protocol.base.a3a4.A3A4PackAdapter
import com.omni.support.ble.utils.BufferConverter

/**
 * @author 邱永恒
 *
 * @time 2019/8/28 17:10
 *
 * @desc
 *
 */
class KeyboxPackAdapter : A3A4PackAdapter() {
    override fun onNotify(buffer: ByteArray): IPack {
        return KeyboxPack.from(buffer)
    }
}