package com.omni.support.ble.protocol.box

import com.omni.support.ble.core.IPack
import com.omni.support.ble.protocol.Pack
import com.omni.support.ble.protocol.base.a3a4.A3A4Pack
import com.omni.support.ble.utils.BufferConverter
import com.omni.support.ble.utils.CRC
import com.omni.support.ble.utils.HexString
import java.util.*
import kotlin.experimental.xor

/**
 * @author 邱永恒
 *
 * @time 2019/9/2 15:35
 *
 * @desc
 *
 */
class BoxPack: A3A4Pack() {
    companion object {
        fun from(buffer: ByteArray): IPack {
            val pack = BoxPack()
            pack.setBuffer(buffer)
            return pack
        }
    }
}