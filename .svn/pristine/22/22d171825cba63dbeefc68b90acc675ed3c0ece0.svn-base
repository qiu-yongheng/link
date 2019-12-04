package com.omni.support.ble.protocol.carport

import android.util.Log
import com.omni.support.ble.core.IPack
import com.omni.support.ble.core.IPackResolver
import com.omni.support.ble.link.LinkGlobalSetting
import com.omni.support.ble.protocol.base.EmptyPack
import com.omni.support.ble.protocol.base.LogPack
import com.omni.support.ble.protocol.base.ReadPack
import com.omni.support.ble.protocol.base.a3a4.A3A4PackAdapter
import com.omni.support.ble.utils.BufferConverter
import com.omni.support.ble.utils.CRC

/**
 * @author 邱永恒
 *
 * @time 2019/8/30 17:49
 *
 * @desc
 *
 */
class CarportPackAdapter : A3A4PackAdapter(){
    override fun onNotify(buffer: ByteArray): IPack {
        return CarportPack.from(buffer)
    }

}