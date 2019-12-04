package com.omni.support.ble.utils

import java.io.File


/**
 * @author 邱永恒
 * @time 2019/7/12 14:05
 * @desc
 */
object DataUtils {

    fun unPack(data: String): Pack {
        val dataBytes = data.toByteArray()

        return unPack(dataBytes)
    }

    fun unPack(file: File): Pack {
        return unPack(file.readBytes())
    }

    /**
     * 拆包
     */
    fun unPack(data: ByteArray): Pack {
        val dataBytesSize = data.size
        val upTotalPack = dataBytesSize / 16 + if (dataBytesSize % 16 == 0) 0 else 1
        val allData = ByteArray(upTotalPack * 16)

        System.arraycopy(data, 0, allData, 0, data.size)

        val upCrcValue = CRC.crc16(allData)

        val upAllPack = Array(upTotalPack) { ByteArray(16) }
        var i = 0
        var j = 0
        while (i < dataBytesSize) {
            if (i + 16 < dataBytesSize)
                System.arraycopy(data, i, upAllPack[j++], 0, 16)
            else
                System.arraycopy(data, i, upAllPack[j++], 0, dataBytesSize - i)
            i += 16
        }
        return Pack(upAllPack, upCrcValue, upTotalPack)
    }

    class Pack(var pack: Array<ByteArray>, var crc: Int, var totalPack: Int)
}
