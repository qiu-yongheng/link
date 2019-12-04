package com.omni.support.ble.protocol.bloom.model

import com.omni.support.ble.core.BufferDeserializable
import com.omni.support.ble.utils.BufferConverter
import kotlin.math.min

/**
 * @author 邱永恒
 *
 * @time 2019/8/23 14:51
 *
 * @desc
 *
 */
class BloomResult : BufferDeserializable {
    var data1: Int = 0
    var data2: Int = 0
    var data3: Int = 0
    var data4: Int = 0

    // 是否开锁
    var isUnlock: Boolean = false
    // 是否有key
    var hasKey: Boolean = false
    // 是否key校验成功
    var isVerifySuccess: Boolean = false
    // 是否蓝牙APP + 按钮开锁模式
    var isAppBtnUnlockMode: Boolean = false
    // 电压
    var voltage: Int = 0

    override fun setBuffer(buffer: ByteArray) {

        val bc = BufferConverter(buffer)
        data1 = bc.u8
        data2 = bc.u8
        data3 = bc.u8
        data4 = bc.u8
        bc.offset(2)
        voltage = bc.u16

        hasKey = data2 and 0x08 == 0x08
        isVerifySuccess = data2 and 0x03 != 0
        isAppBtnUnlockMode = data3 and 0x04 == 0x04
        isUnlock = data3 and 0x01 == 0
    }

    override fun toString(): String {
        return "BloomResult(isUnlock=$isUnlock, hasKey=$hasKey, isVerifySuccess=$isVerifySuccess, isAppBtnUnlockMode=$isAppBtnUnlockMode, voltage=$voltage)"
    }


}