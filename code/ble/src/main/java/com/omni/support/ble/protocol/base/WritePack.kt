package com.omni.support.ble.protocol.base

import com.omni.support.ble.exception.BleException
import com.omni.support.ble.protocol.Pack
import com.omni.support.ble.utils.BufferConverter
import com.omni.support.ble.utils.CRC

/**
 * @author 邱永恒
 *
 * @time 2019/8/20 10:45
 *
 * @desc 写
 *
 */
class WritePack : Pack() {

    override fun getBuffer(): ByteArray {
        if (payload.size != 18) {
            throw BleException(BleException.ERR_PAYLOAD_SIZE_FAIL, "payload长度不合法, 目标: 18, 当前: ${payload.size}")
        }

        val bc = BufferConverter(20)

        val crc16 = CRC.crc16(payload)
        bc.putU16(crc16)
        bc.putBytes(payload)

        return bc.buffer()
    }

    override fun setBuffer(buffer: ByteArray) {
    }
}