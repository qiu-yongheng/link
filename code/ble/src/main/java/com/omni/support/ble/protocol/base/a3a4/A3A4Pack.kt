package com.omni.support.ble.protocol.base.a3a4

import com.omni.support.ble.protocol.Pack
import com.omni.support.ble.utils.BufferConverter
import com.omni.support.ble.utils.CRC
import com.omni.support.ble.utils.HexString
import java.util.*
import kotlin.experimental.xor

/**
 * @author 邱永恒
 *
 * @time 2019/9/3 10:20
 *
 * @desc
 *
 */
open class A3A4Pack : Pack() {
    override fun getBuffer(): ByteArray {
        val payloadSize = payload.size
        val frameSize = 6 + payloadSize
        val bc = BufferConverter(frameSize)

        // 协议帧
        bc.putU8(PREFIX_1)
        bc.putU8(PREFIX_2)
        // len
        bc.putU8(payloadSize)
        // 随机数
        bc.putU8(Random().nextInt(255))
        // key
        bc.putU8(key)
        // cmd
        bc.putU8(cmd)
        // payload
        bc.putBytes(payload)

        debug("send origin " + bc.buffer().size + " bytes: " + HexString.valueOf(bc.buffer()))

        // 加密
        val command = bc.buffer()
        val xorCommand = scooterDecode(command)

        // CRC8
        val crc = CRC.crc8(xorCommand)
        val crcOrder = BufferConverter(frameSize + 1)
        crcOrder.putBytes(xorCommand)
        crcOrder.putU8(crc)

        return crcOrder.buffer()
    }

    override fun setBuffer(buffer: ByteArray) {
        if (CRC.checkCRC8(buffer)) {
            val bc = BufferConverter(buffer)

            val prefix1 = bc.u8
            val prefix2 = bc.u8
            val len = bc.u8
            val random = (bc.u8 - 0x32) and 0xFF
            val key = bc.u8 xor random
            cmd = bc.u8 xor random
            debug("recv: cmd= ${HexString.valueOf(cmd)}, random= $random")

            if (len < 0) {
                debug("len error: $len")
                return
            }

            val payload = bc.getBytes(len)

            val data = ByteArray(len)
            payload.forEachIndexed { index, byte ->
                data[index] = byte xor random.toByte()
            }
            this.payload = data
        } else {
            debug("crc校验失败")
        }
    }

    companion object {
        const val PREFIX_1 = 0xA3
        const val PREFIX_2 = 0xA4
        const val TRANSMISSION = 0xFB
    }
}