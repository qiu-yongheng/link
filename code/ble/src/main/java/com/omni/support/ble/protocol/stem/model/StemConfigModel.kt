package com.omni.support.ble.protocol.stem.model

import android.util.Log
import com.omni.support.ble.core.BufferDeserializable
import com.omni.support.ble.core.BufferSerializable
import com.omni.support.ble.utils.BufferConverter
import java.util.*

/**
 * @author 邱永恒
 *
 * @time 2019/11/12 9:10
 *
 * @desc
 *
 */
class StemConfigModel() : BufferSerializable, BufferDeserializable {
    // 车轮周长(厘米)
    var wheelPerimeter: Int = 0
    // 防盗锁车(1-启用, 0-停用)
    var isAntitheft: Boolean = true
    // 校验秘钥
    var connectKey: Int = 0
    var isSuccess: Boolean = false


    constructor(wheelPerimeter: Int, isAntitheft: Boolean, connectKey: Int) : this() {
        this.wheelPerimeter = wheelPerimeter
        this.isAntitheft = isAntitheft
        this.connectKey = connectKey
    }

    override fun getBuffer(): ByteArray {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH) + 1
        val day = c.get(Calendar.DAY_OF_MONTH)
        val hour = c.get(Calendar.HOUR_OF_DAY)
        val minute = c.get(Calendar.MINUTE)
        val second = c.get(Calendar.SECOND)

        val bc = BufferConverter(16)

        // 时间
        bc.putU8(year % 100)
        bc.putU8(month)
        bc.putU8(day)
        bc.putU8(hour)
        bc.putU8(minute)
        bc.putU8(second)
        // 车轮周长
        bc.putU16(wheelPerimeter)
        // 防盗锁
        bc.putU8(if (isAntitheft) 1 else 0)
        // offset
        bc.offset(3)
        // key
        val key1 = connectKey shr 16 and 0xff
        val key2 = connectKey shr 8 and 0xff
        val key3 = connectKey and 0xff
        val key4 = (key1 + key2 + key3) and 0xff
        bc.putU8(key1)
        bc.putU8(key2)
        bc.putU8(key3)
        bc.putU8(key4)

        return bc.buffer()
    }

    override fun setBuffer(buffer: ByteArray) {
        if (buffer.size != 16) {
            return
        }

        val bc = BufferConverter(buffer)
        bc.offset(6)
        wheelPerimeter = bc.u16
        isAntitheft = bc.u8 == 1
        bc.offset(3)
        val result1 = bc.u8
        val result2 = bc.u8
        val result3 = bc.u8
        val result4 = bc.u8
        isSuccess = (result1 + result2 + result3 + result4) == 4
    }

    override fun toString(): String {
        return "StemConfigModel(wheelPerimeter=$wheelPerimeter, isAntitheft=$isAntitheft, isSuccess=$isSuccess)"
    }
}