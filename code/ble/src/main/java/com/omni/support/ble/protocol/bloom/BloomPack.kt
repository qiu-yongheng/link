package com.omni.support.ble.protocol.bloom

import com.omni.support.ble.core.IPack
import com.omni.support.ble.protocol.Pack
import com.omni.support.ble.protocol.base.abde.ABDEPack
import com.omni.support.ble.protocol.keybox.KeyboxPack
import com.omni.support.ble.utils.BufferConverter
import com.omni.support.ble.utils.HexString
import java.util.*
import kotlin.experimental.xor

/**
 * @author 邱永恒
 *
 * @time 2019/8/29 16:44
 *
 * @desc 钢缆锁
 *
 */
class BloomPack : ABDEPack() {

    companion object {

        fun from(buffer: ByteArray): IPack {
            val pack = BloomPack()
            pack.cmd = RESULT_CMD
            pack.setBuffer(buffer)
            return pack
        }
    }
}