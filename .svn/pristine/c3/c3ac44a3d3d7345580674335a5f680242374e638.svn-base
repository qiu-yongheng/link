package com.omni.support.ble.protocol.base.fe

import com.omni.support.ble.protocol.Pack
import com.omni.support.ble.utils.BufferConverter
import com.omni.support.ble.utils.CRC
import com.omni.support.ble.utils.HexString
import java.util.*
import kotlin.experimental.xor

/**
 * @author 邱永恒
 *
 * @time 2019/9/3 10:47
 *
 * @desc
 *
 */
open class FEPack: Pack() {
    override fun getBuffer(): ByteArray {
        val payloadSize = payload.size
        val frameSize = 5 + payloadSize
        val bc = BufferConverter(frameSize)

        // 协议帧
        bc.putU8(PREFIX)
        // 随机数
//        bc.putU8(Random().nextInt(255))
        bc.putU8(0)
        // key
        bc.putU8(key)
        // cmd
        bc.putU8(cmd)
        // len
        bc.putU8(payloadSize)
        // payload
        bc.putBytes(payload)

        debug("send origin " + bc.buffer().size + " bytes: " + HexString.valueOf(bc.buffer()))

        // 加密
        val command = bc.buffer()
        val xorCommand = bikeDecode(command)

        // CRC16
        val crc = CRC.crc16(xorCommand)
        val crcOrder = BufferConverter(frameSize + 2)
        crcOrder.putBytes(xorCommand)
        crcOrder.putU16(crc)

        return crcOrder.buffer()
    }

    override fun setBuffer(buffer: ByteArray) {
        if (CRC.checkCRC16(buffer)) {
            val bc = BufferConverter(buffer)

            val prefix = bc.u8
            val random = (bc.u8 - 0x32) and 0xFF
            val key = bc.u8 xor random
            cmd = bc.u8 xor random
            debug("recv: cmd= ${HexString.valueOf(cmd)}, random= $random")
            val len = bc.u8 xor random
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
        const val PREFIX = 0xFE
    }
}