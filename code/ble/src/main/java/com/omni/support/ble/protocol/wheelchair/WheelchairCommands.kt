package com.omni.support.ble.protocol.wheelchair

import com.omni.support.ble.protocol.Command
import com.omni.support.ble.protocol.base.model.ReadResult
import com.omni.support.ble.protocol.base.model.StartReadResult
import com.omni.support.ble.protocol.wheelchair.model.*
import com.omni.support.ble.rover.annotations.*

/**
 * @author 邱永恒
 *
 * @time 2019/8/7 17:12
 *
 * @desc
 *
 */
interface WheelchairCommands {
    /**
     * 获取蓝牙通讯key
     */
    @CommandID(0x11)
    fun getKey(deviceKey: String): Command<WcKeyResult>

    /**
     * 开锁
     * 如果已开锁, 没有回调
     * @param timestamp 秒单位时间戳
     * @param optType 0:围栏内开锁，1：围栏外开锁
     */
    @CommandID(0x21)
    fun unlock(@S32 uid: Int, @U32 timestamp: Long, @U8 optType: Int): Command<Boolean>

    /**
     * 开锁回复
     */
    @NoResponse
    @CommandID(0x21)
    fun unlockReply(): Command<Void>

    /**
     * 关锁监听
     */
    @CommandID(0x22)
    fun lock(): Command<WcLockResult>

    /**
     * 关锁回复
     * 返回上锁失败后，设备会自动开锁
     */
    @NoResponse
    @CommandID(0x22)
    fun lockReply(): Command<Void>

    /**
     * 获取锁状态信息
     */
    @CommandID(0x31)
    fun getLockInfo(): Command<WcInfoResult>

    /**
     * 获取未上传数据
     */
    @CommandID(0x51)
    fun getOldData(): Command<WcOldDataResult>

    /**
     * 清除未上传数据
     */
    @CommandID(0x52)
    fun cleanOldData(): Command<Boolean>

    /**
     * 轮椅 ID  错误通知
     * 轮椅锁发出给APP，有APP判断ID是否正确，不正确发送60指令
     */
    @CommandID(0x60)
    fun updateIdError(@U32 lockId: Long, @U8 reserve: Int = 0): Command<WcUpdateIdErrorResult>

    /**
     * 关机
     */
    @CommandID(0x90)
    fun shutdown(): Command<WcShutdownResult>

    /**
     * 获取固件信息包数
     */
    @CommandID(0xFA)
    fun startRead(): Command<StartReadResult>

    /**
     * 获取固件信息
     * @param pack 包数
     * @param deviceType 设备类型, 如果不正确, 不会返回数据
     */
    @CommandID(0xFB)
    fun read(@U16 pack: Int, @U8 deviceType: Int): Command<ReadResult>
}