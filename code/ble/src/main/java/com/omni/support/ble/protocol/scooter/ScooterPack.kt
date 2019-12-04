package com.omni.support.ble.protocol.scooter

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
 * @time 2019/8/23 17:40
 *
 * @desc
 *
 */
class ScooterPack: A3A4Pack() {

    companion object {
        fun from(buffer: ByteArray): IPack {
            val pack = ScooterPack()
            pack.setBuffer(buffer)
            return pack
        }
    }
}