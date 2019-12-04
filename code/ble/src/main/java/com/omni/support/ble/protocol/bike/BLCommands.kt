package com.omni.support.ble.protocol.bike

import com.omni.support.ble.protocol.*
import com.omni.support.ble.protocol.base.LogCommand
import com.omni.support.ble.protocol.base.TransmissionCommand
import com.omni.support.ble.protocol.base.model.ReadResult
import com.omni.support.ble.protocol.base.model.StartReadResult
import com.omni.support.ble.protocol.base.model.UpgradeResult
import com.omni.support.ble.protocol.base.model.WriteResult
import com.omni.support.ble.protocol.bike.model.*
import com.omni.support.ble.rover.annotations.*

/**
 * @author 邱永恒
 *
 * @time 2019/8/7 17:12
 *
 * @desc
 *
 */
interface BLCommands {
    /**
     * 获取蓝牙通讯key
     */
    @CommandID(0x11)
    fun getKey(deviceKey: String): Command<BLKeyResult>

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
     * 关锁回复
     */
    @NoResponse
    @CommandID(0x22)
    fun lockReply(): Command<Void>

    /**
     * 关锁监听
     */
    @CommandID(0x22)
    fun lock(): Command<BLLockResult>

    /**
     * 获取锁状态信息
     */
    @CommandID(0x31)
    fun getLockInfo(): Command<BLInfoResult>

    /**
     * 获取未上传数据
     */
    @CommandID(0x51)
    fun getOldData(): Command<BLOldDataResult>

    /**
     * 清除未上传数据
     */
    @CommandID(0x52)
    fun cleanOldData(): Command<Boolean>

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

    /**
     * 发送数据完成
     * @param cmd 获取数据的指令（例：0xFD 指令获取数据完成后，发送此指令，此处填写 0xFD）
     */
    @NoResponse
    @CommandID(0xFF)
    fun endTransmission(@U8 cmd: Int): Command<Void>

    /**
     * 启动修改车锁信息
     * 没有回应, 发送后开始发送pack
     */
    @CommandID(0xFC, 0xFD)
    fun startWrite(@U16 totalPack: Int, @U16 crc: Int, @U8 deviceType: Int, @U32 reserve: Long = 0): Command<WriteResult>

    /**
     * 传输pack
     */
    @CommandID(0, 0xFD)
    fun write(@U16 pack: Int, data: ByteArray): TransmissionCommand<WriteResult>

    /**
     * 发送最后一包
     */
    @CommandID(0, 0xFF)
    fun endWrite(@U16 pack: Int, data: ByteArray): TransmissionCommand<BLEndTransmissionResult>

    /**
     * 开始升级
     */
    @CommandID(0xF0, 0xF1)
    fun startUpgrade(@U16 totalPack: Int, @U16 crc: Int, @U8 deviceType: Int, updateKey: String): Command<UpgradeResult>

    /**
     * 升级
     * 最后一包要发送 endWrite()
     */
    @CommandID(0, 0xF1)
    fun upgrade(@U16 pack: Int, data: ByteArray): TransmissionCommand<UpgradeResult>

    /**
     * 关机
     */
    @CommandID(0x90)
    fun shutdown(): Command<BLShutdownResult>

    /**
     * 读取日志
     */
    @CommandID(0xCC, 0xC7)
    fun getLog(@U8 data: Int = 0): LogCommand<String>
}