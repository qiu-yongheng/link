package com.omni.support.ble.protocol.stem.model

import com.omni.support.ble.core.BufferSerializable
import com.omni.support.ble.utils.BufferConverter

/**
 * @author 邱永恒
 *
 * @time 2019/11/12 17:47
 *
 * @desc
 *
 */
class StemControlparam : BufferSerializable {
    // 锁车
    var lockState: Int = 0
    // 报警
    var alarmState: Int = 0
    // 灯光
    var lightState: Int = 0
    // 水平校准
    var horizontalCalibration: Int = 0
    // 关机(关闭广播)
    var shutdown: Int = 0
    // 恢复出厂设置(取消配对)
    var reset: Int = 0

    companion object {
        // 关锁
        const val LOCK = 1
        // 开锁
        const val UNLOCK = 2

        // 启动水平校准
        const val HORIZONTAL_CALIBRATION_OPEN = 1

        // 找车
        const val ALARM_FIND = 2

        // 关闭
        const val LIGHT_CLOSE = 1
        // 半亮
        const val LIGHT_HALF_BRIGHT = 2
        // 全亮
        const val LIGHT_ALL_BRIGHT = 3
        // 闪烁
        const val LIGHT_FLICKER = 4

        const val SHUTDOWN = 1

        const val RESET = 1

    }

    override fun getBuffer(): ByteArray {
        val bc = BufferConverter(6)
        bc.putU8(lockState)
        bc.putU8(alarmState)
        bc.putU8(lightState)
        bc.putU8(horizontalCalibration)
        bc.putU8(shutdown)
        bc.putU8(reset)

        return bc.buffer()
    }
}