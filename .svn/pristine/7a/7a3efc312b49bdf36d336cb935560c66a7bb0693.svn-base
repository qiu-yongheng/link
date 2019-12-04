package com.omni.support.ble.protocol.box

import com.omni.support.ble.protocol.Command
import com.omni.support.ble.protocol.base.model.KeyResult
import com.omni.support.ble.protocol.base.model.UnlockResult
import com.omni.support.ble.protocol.box.model.BoxLockInfoResult
import com.omni.support.ble.protocol.box.model.BoxLowUnlockConfigResult
import com.omni.support.ble.protocol.scooter.model.ScooterLockInfoResult
import com.omni.support.ble.rover.annotations.CommandID
import com.omni.support.ble.rover.annotations.U32
import com.omni.support.ble.rover.annotations.U8

/**
 * @author 邱永恒
 *
 * @time 2019/9/2 15:38
 *
 * @desc
 *
 */
interface BoxCommands {
    /**
     * 获取蓝牙通讯key
     */
    @CommandID(0x01)
    fun getKey(deviceKey: String): Command<KeyResult>

    /**
     * 开锁
     * @param mode 0x01, 开锁
     * @param unlockType 0->正常开锁 0xA0->开锁时不重置骑行时间
     */
    @CommandID(0x05)
    fun unlock(@U8 mode: Int = 0x01, @U32 uid: Long, @U32 timestamp: Long, @U8 unlockType: Int): Command<UnlockResult>

    /**
     * 低电量自动开锁配置
     * @param data 1开启; 0关闭
     */
    @CommandID(0x35)
    fun lowUnlockConfig(@U8 mode: Int = 0x01, @U8 data: Int): Command<BoxLowUnlockConfigResult>

    /**
     * 获取锁信息
     */
    @CommandID(0x31)
    fun getLockInfo(@U8 control: Int = 0x01): Command<BoxLockInfoResult>
}