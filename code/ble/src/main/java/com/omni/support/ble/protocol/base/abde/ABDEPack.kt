package com.omni.support.ble.protocol.base.abde

import com.omni.support.ble.protocol.Pack
import com.omni.support.ble.utils.BufferConverter
import com.omni.support.ble.utils.HexString
import java.util.*
import kotlin.collections.ArrayList
import kotlin.experimental.and
import kotlin.experimental.xor

/**
 * @author 邱永恒
 *
 * @time 2019/9/3 10:42
 *
 * @desc
 *
 */
open class ABDEPack : Pack() {
    var keyOrg: ByteArray = byteArrayOf(
        0x61, 0x66, 0x6B, 0x33, 0x74, 0x79, 0x73, 0x77,
        0x34, 0x70, 0x73, 0x6B, 0x32, 0x36, 0x68, 0x6A
    )
    private var inputKey = byteArrayOf(0x1, 0x2, 0x3, 0x4)

    override fun getBuffer(): ByteArray {
        val payloadSize = payload.size
        val frameSize = 5 + payloadSize
        val bc = BufferConverter(frameSize)

        // 协议帧
        bc.putU8(PREFIX_1)
        bc.putU8(PREFIX_2)
        // len = 随机数 + cmd + payload + crc
        bc.putU8(payloadSize + 3)
        // 随机数: 校验key必须使用返回的payload的第九个字节
        bc.putU8(random)
        // cmd
        bc.putU8(cmd)
        // payload
        bc.putBytes(payload)

        debug("send origin " + bc.buffer().size + " bytes: " + HexString.valueOf(bc.buffer()))

        // 加密字典
        return if (hasLocalKey) {
            val keyMap = getKeyMap(inputKey, keyOrg)
            val command = bc.buffer()
            encryption(command, keyMap)
        } else {
            val command = bc.buffer()
            encryptionWithOrgkey(command, keyOrg)
        }
    }

    override fun setBuffer(buffer: ByteArray) {
        val bc = BufferConverter(buffer)
        val prefix1 = bc.u8
        val prefix2 = bc.u8
        val len = bc.u8

        if (len < 0) {
            debug("len error: $len")
            return
        }

        val payload = bc.getBytes(len)
        this.payload = payload
        // 这个random不是返回的random, 取payload[8]用来当做校验的随机数
        // 如果锁本地没有key, random = 0
        random = payload[8].toInt() and 0xFF
        hasLocalKey = payload[1].toInt() and 0x08 != 0
        debug("随机数: ${HexString.valueOf(random)}, 本地是否有key: $hasLocalKey")
    }

    /**
     * 获取加密字典
     */
    private fun getKeyMap(inputKey: ByteArray, keyOrg: ByteArray): ByteArray {
        val keyMap = ByteArray(256)
        var index = 0
        for (k in 0..3) {
            for (i in inputKey.indices) {
                for (j in keyOrg.indices) {
                    keyMap[index] = ((inputKey[i] + 0x30 + k) xor keyOrg[j].toInt()).toByte()
                    index++
                }
            }
        }
        return keyMap
    }

    /**
     * 加密
     */
    private fun encryption(cmd: ByteArray, keyMap: ByteArray): ByteArray {
        var random = cmd[3].toInt() and 0xFF
        val xorplain = ByteArray(cmd.size)
        for (i in cmd.indices) {
            if (i >= 5) {
                // 加密
                if (random >= keyMap.size) random = 0
                xorplain[i] = cmd[i] xor (keyMap[random++].toInt() and 0xFF).toByte()
            } else {
                // payload前的数据都不需要加密
                xorplain[i] = cmd[i]
            }
        }
        return checkSum(xorplain)
    }

    /**
     * 加密(写入key)
     */
    private fun encryptionWithOrgkey(cmd: ByteArray, keyMap: ByteArray): ByteArray {
        var index = 0
        val xorplain = ByteArray(cmd.size)
        for (i in cmd.indices) {
            if (i >= 5) {
                // 加密
                xorplain[i] = cmd[i] xor (keyMap[index++].toInt() and 0xFF).toByte()
            } else {
                // payload前的数据都不需要加密
                xorplain[i] = cmd[i]
            }
        }
        return checkSum(xorplain)
    }

    /**
     * 获取校验和
     */
    private fun checkSum(data: ByteArray): ByteArray {
        var sum = 0
        val result = ByteArray(data.size + 1)
        for (i in data.indices) {
            result[i] = data[i]
            // 从随机数开始校验和
            if (i >= 3) {
                sum += data[i].toInt()
            }
        }
        result[data.size] = (sum and 0xFF).toByte()
        return result
    }

    companion object {
        const val PREFIX_1 = 0xAB
        const val PREFIX_2 = 0xDE

        // 返回数据没有cmd, 统一使用0xFF, 用来给sessionCall接收数据
        const val RESULT_CMD = 0xFF

        var random = 0
        var hasLocalKey: Boolean = false
    }
}