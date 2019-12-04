package com.omni.support.ble.protocol.ulock

import com.omni.support.ble.core.IPack
import com.omni.support.ble.protocol.Pack
import com.omni.support.ble.protocol.base.fe.FEPack
import com.omni.support.ble.protocol.bike.BLPack
import com.omni.support.ble.utils.BufferConverter
import com.omni.support.ble.utils.CRC
import com.omni.support.ble.utils.HexString
import java.util.*
import kotlin.experimental.xor

/**
 * @author 邱永恒
 *
 * @time 2019/8/30 13:39
 *
 * @desc
 *
 */
class ULockPack: FEPack() {
    companion object {
        fun from(buffer: ByteArray): IPack {
            val pack = ULockPack()
            pack.setBuffer(buffer)
            return pack
        }
    }
}