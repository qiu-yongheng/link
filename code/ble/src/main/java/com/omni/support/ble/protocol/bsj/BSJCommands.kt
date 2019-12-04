package com.omni.support.ble.protocol.bsj

import com.omni.support.ble.protocol.Command
import com.omni.support.ble.protocol.base.model.EmptyResult
import com.omni.support.ble.protocol.bsj.model.*
import com.omni.support.ble.rover.annotations.CommandID
import com.omni.support.ble.rover.annotations.U8

/**
 * @author 邱永恒
 *
 * @time 2019/9/5 18:25
 *
 * @desc
 *
 */
interface BSJCommands {

    /**
     * 获取令牌
     */
    @CommandID(0x0101, 0x0102)
    fun getToken(@U8 data: Int = 0x01): Command<BSJTokenResult>

    /**
     * 修改AES秘钥
     * @param key 8个字节, 发两包
     */
    @CommandID(0x0301, 0x0303)
    fun modifyAesKey(key: ByteArray): Command<Boolean>

    /**
     * 修改开锁密码
     * @param pwd 6个字节, 先发旧密码, 再发新密码
     */
    @CommandID(0x0304, 0x0306)
    fun modifyUnlockKey(pwd: ByteArray): Command<Boolean>

    /**
     * 修改广播名字
     * @param name 9个字节, 发两包, 最大长度18, 不足补零
     */
    @CommandID(0x0307, 0x0309)
    fun modifyReciverName(name: ByteArray): Command<Boolean>

    /**
     * 查询是否需要充电
     */
    @CommandID(0x030A, 0x030B)
    fun getBatteryStatus(): Command<BSJBatteryStatusResult>

    /**
     * 开锁
     * @param pwd 6个字节
     */
    @CommandID(0x0201, 0x0202)
    fun unlock(pwd: ByteArray, @U8 data: Int = 10): Command<Boolean>

    /**
     * 复位重启
     */
    @CommandID(0x0203, 0x0204)
    fun reboot(@U8 data: Int = 0x01): Command<Boolean>

    /**
     * 获取指纹锁状态
     * 返回多个包, 使用asyncCall操作符接收
     */
    @CommandID(0x0501, 0x0502)
    fun getFingerprintLockStatus(): Command<BSJFingerLockStatusResult>

    /**
     * 录入指纹
     * 返回多次结果, 需要主动通知退出录入指纹, 使用subscribe操作符订阅回调
     */
    @CommandID(0x0503, 0x0504)
    fun entryFingerprint(): Command<BSJEntryFingerprintResult>

    /**
     * 退出指纹录入
     * 当APP收到指纹录入响应成功后，APP与服务器同步，同步成功后向蓝牙发送指纹录入成功确认
     */
    @CommandID(0x0509)
    fun finishEntry(): Command<Boolean>

    /**
     * 删除指纹
     * @param fId 需要删除的指纹ID, 如果要删除所有指纹, 输入0xFF
     */
    @CommandID(0x0505, 0x0506)
    fun deleteFingerprint(@U8 fId: Int): Command<BSJDeleteFingerResult>

    /**
     * 获取指纹ID列表
     * 返回多个包, 使用asyncCall操作符接收
     */
    @CommandID(0x0507, 0x0508)
    fun getFingerList(): Command<BSJFingerLockListResult>

    /**
     * 蓝牙模块与指纹模块通讯超时
     * 使用subscribe操作符订阅回调
     */
    @CommandID(0x050A)
    fun fingerTimeout(): Command<EmptyResult>
}