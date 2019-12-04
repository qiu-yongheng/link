package com.omni.support.ble.protocol.carport

import com.omni.support.ble.protocol.*
import com.omni.support.ble.protocol.base.LogCommand
import com.omni.support.ble.protocol.base.TransmissionCommand
import com.omni.support.ble.protocol.base.model.*
import com.omni.support.ble.protocol.bike.model.BLEndTransmissionResult
import com.omni.support.ble.protocol.carport.model.*
import com.omni.support.ble.rover.annotations.*

/**
 * @author 邱永恒
 *
 * @time 2019/8/30 17:54
 *
 * @desc
 *
 */
interface CarportCommands {
    /**
     * 获取蓝牙通讯key
     */
    @CommandID(0x01)
    fun getKey(deviceKey: String): Command<KeyResult>

    /**
     * 错误监听
     */
    @CommandID(0x10)
    fun error(): Command<ErrorResult>

    /**
     * 修改蓝牙key
     */
    @CommandID(0x33)
    fun modifyDeviceKey(@U8 mode: Int = 0x01, deviceKey: String): Command<CarportModifyDeviceKeyResult>

    /**
     * 修改蓝牙key回复
     */
    @NoResponse
    @CommandID(0x33)
    fun modifyDeviceKeyReply(@U8 mode: Int = 0x02): Command<Void>

    /**
     * 读取旧数据
     */
    @CommandID(0x51)
    fun getOldData(@U8 data: Int = 0x01): Command<OldDataResult>

    /**
     * 清除旧数据
     */
    @CommandID(0x52)
    fun cleanOldData(@U8 data: Int = 0x01): Command<CarportCleanOldDataResult>

    /**
     * 配对(保存mac地址)
     * 返回多次响应
     */
    @CommandID(0x06)
    fun pair(@U8 mode: Int = 0x01, mac: ByteArray): Command<CarportPairResult>

    /**
     * 获取设备本身的mac地址
     */
    @CommandID(0x03)
    fun getMac(@U8 mode: Int = 0x01): Command<CarportGetMacResult>

    /**
     * 获取配对遥控器信息
     */
    @CommandID(0x04)
    fun getRCInfo(@U8 mode: Int = 0x01): Command<CarportGetMacResult>

    /**
     * 不拦车
     * 返回多次响应
     * @param mode 不拦车模式: 1：手动模式; 3:自动模式(当APP与车位锁断开连接，车位锁会自动回到拦车状态)
     */
    @CommandID(0x05)
    fun carportDown(@U8 mode: Int, @U32 uid: Long, @U32 timestamp: Long, @U8 reserve: Int = 0x00): Command<CarportUnlockResult>

    /**
     * 不拦车回复
     */
    @NoResponse
    @CommandID(0x05)
    fun downReply(@U8 mode: Int = 0x02): Command<Void>

    /**
     * 拦车
     * 返回多次响应
     */
    @CommandID(0x15)
    fun carportUp(@U8 mode: Int = 0x01): Command<CarportLockResult>

    /**
     * 拦车回复
     */
    @NoResponse
    @CommandID(0x15)
    fun upReply(@U8 mode: Int = 0x02): Command<Void>

    /**
     * 遥控器设置模式
     * @param data 1手动模式，2自动模式
     */
    @CommandID(0x07)
    fun setRcMode(@U8 mode: Int = 0x01, @U8 data: Int): Command<CarportSetModeResult>

    /**
     * 模式设置回复
     */
    @NoResponse
    @CommandID(0x07)
    fun rcModeReply(@U8 mode: Int = 0x02): Command<Void>

    /**
     * 获取日志
     */
    @CommandID(0xCC, 0xC7)
    fun getLog(@U8 deviceType: Int): LogCommand<String>

    /**
     * 获取锁信息
     */
    @CommandID(0x31)
    fun getLockInfo(@U8 mode: Int = 0x01): Command<CarportLockInfoResult>

    /**
     * 获取固件信息包数
     */
    @CommandID(0xFA, 0xFB)
    fun startRead(): Command<CarportStartReadResult>

    /**
     * 获取固件信息
     * @param pack 包数
     * @param deviceType 设备类型, 如果不正确, 不会返回数据
     */
    @CommandID(0xFC, 0xFB)
    fun read(@U16 pack: Int, @U8 deviceType: Int): Command<ReadResult>

    /**
     * 开始写信息
     * @param type 0x00发送升级包, 0x01发送修改配置包
     */
    @CommandID(0xFB, 0xFC)
    fun startWrite(@U8 type: Int, @U16 totalPack: Int, @U16 crc: Int, @U8 deviceType: Int, updateKey: String): Command<WriteResult>

    /**
     * 传输pack
     */
    @CommandID(0, 0xFC)
    fun write(@U16 pack: Int, data: ByteArray): TransmissionCommand<WriteResult>

    /**
     * 发送最后一包, 没有响应
     */
    @NoResponse
    fun endWrite(@U16 pack: Int, data: ByteArray): TransmissionCommand<Void>
}