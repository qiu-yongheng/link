package com.omni.support.ble.protocol.keybox

import com.omni.support.ble.protocol.Command
import com.omni.support.ble.protocol.base.model.EmptyResult
import com.omni.support.ble.protocol.keybox.model.*
import com.omni.support.ble.protocol.base.model.ErrorResult
import com.omni.support.ble.rover.annotations.*

/**
 * @author 邱永恒
 *
 * @time 2019/8/28 17:13
 *
 * @desc
 *
 */
interface KeyboxCommands {
    /**
     * 获取蓝牙通讯key
     */
    @CommandID(0x01)
    fun getKey(deviceKey: String): Command<KeyboxKeyResult>

    /**
     * 错误监听
     */
    @CommandID(0x10)
    fun error(): Command<ErrorResult>

    /**
     * 获取锁信息
     */
    @CommandID(0x31)
    fun getLockInfo(@U8 data: Int = 0x01): Command<KeyboxLockInfoResult>

    /**
     * 设置报警
     * @param data 0x00 报警关, 0x01 报警开
     */
    @CommandID(0x03)
    fun setAlert(@U8 data: Int): Command<KeyboxAlertResult>

    /**
     * 开锁
     * @param mode 0x01, 开锁
     * @param unlockType 0->正常开锁 0xA0->开锁时不重置骑行时间
     */
    @CommandID(0x21)
    fun unlock(@U8 mode: Int = 0x01, @U32 uid: Long, @U32 timestamp: Long, @U8 unlockType: Int = 0x00): Command<KeyboxUnlockResult>

    /**
     * 开锁回复
     * @param mode 0x02, 回复
     */
    @NoResponse
    @CommandID(0x21)
    fun unlockReply(@U8 mode: Int = 0x02): Command<Void>

    /**
     * 关锁监听
     */
    @CommandID(0x22)
    fun lock(): Command<KeyboxLockResult>

    /**
     * 关锁回复
     * @param mode 0x02, 回复
     */
    @NoResponse
    @CommandID(0x22)
    fun lockReply(@U8 mode: Int = 0x02): Command<Void>

    /**
     * 设置同步时间戳
     * @param timestamp 秒
     */
    @CommandID(0x02)
    fun setTimestamp(@U32 timestamp: Long): Command<KeyboxSetTimestampResult>

    /**
     * 拔钥匙监听
     */
    @CommandID(0x06)
    fun pullOut(): Command<KeyboxKeyStatusResult>

    /**
     * 拔钥匙回复
     */
    @NoResponse
    @CommandID(0x06)
    fun pullOutReply(@U8 mode: Int = 0x02): Command<Void>

    /**
     * 放回钥匙监听
     */
    @CommandID(0x07)
    fun putBack(): Command<KeyboxKeyStatusResult>

    /**
     * 放回钥匙回复
     */
    @NoResponse
    @CommandID(0x07)
    fun putBackReply(@U8 mode: Int = 0x02): Command<Void>

    /**
     * 获取开锁记录, 返回记录的index, 必须要回复锁, 不然会一直发
     * 返回多条记录, 使用subscribe操作符接收
     */
    @CommandID(0x51)
    fun getUnlockRecord(@U8 mode: Int = 0x01): Command<KeyboxLockRecordResult>

    /**
     * 收到开锁记录回复
     */
    @NoResponse
    @CommandID(0x51)
    fun recordReply(@U8 mode: Int = 0x02, @U16 index: Int): Command<Void>

    /**
     * 清除开锁记录
     */
    @NoResponse
    @CommandID(0x52)
    fun cleanUnlockRecord(@U8 mode: Int = 0x01): Command<Void>

    /**
     * 临时密码控制(只能使用一次)
     * 备份密码必须是8位
     * @param mode 0x01写入密码; 0x02查询密码; 0x03删除密码; 0x04清除所有密码
     * @param pwd 如果mode是0x04, 传入0
     */
    @CommandID(0x53)
    fun tempPwdControl(@U8 mode: Int, @U32 pwd: Long): Command<KeyboxTempPwdControlResult>

    /**
     * 随机码配置
     * @param maxLen    密码的最大长度(最大9位)
     * @param validTime 密码的有效时间(S)
     */
    @CommandID(0x55)
    fun randomPwdConfig(@U8 maxLen: Int, @U32 validTime: Long): Command<KeyboxRandomCodeConfigResult>

    /**
     * 修改蓝牙key
     */
    @CommandID(0x33)
    fun modifyDeviceKey(@U8 mode: Int = 0x01, deviceKey: String): Command<KeyboxModifyDeviceKeyResult>

    /**
     * 重置到默认蓝牙key
     */
    @CommandID(0x33)
    fun resetDeviceKey(@U8 mode: Int = 0x02): Command<KeyboxModifyDeviceKeyResult>

    /**
     * 关机
     */
    @CommandID(0x13)
    fun shutdown(@U8 mode: Int = 0x00, @U8 data: Int = 0x01): Command<EmptyResult>
}