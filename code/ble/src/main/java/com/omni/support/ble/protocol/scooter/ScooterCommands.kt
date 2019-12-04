package com.omni.support.ble.protocol.scooter

import com.omni.support.ble.protocol.*
import com.omni.support.ble.protocol.base.LogCommand
import com.omni.support.ble.protocol.base.TransmissionCommand
import com.omni.support.ble.protocol.base.model.ErrorResult
import com.omni.support.ble.protocol.base.model.ReadResult
import com.omni.support.ble.protocol.base.model.WriteResult
import com.omni.support.ble.protocol.scooter.model.*
import com.omni.support.ble.rover.annotations.*

/**
 * @author 邱永恒
 *
 * @time 2019/8/23 14:50
 *
 * @desc
 *
 */
interface ScooterCommands {
    /**
     * 获取蓝牙通讯key
     */
    @CommandID(0x01)
    fun getKey(deviceKey: String): Command<ScooterKeyResult>

    /**
     * 错误监听
     */
    @CommandID(0x10)
    fun error(): Command<ErrorResult>

    /**
     * 开锁
     * @param mode 0x01, 开锁
     * @param unlockType 0->正常开锁 0xA0->开锁时不重置骑行时间
     */
    @CommandID(0x05)
    fun unlock(@U8 mode: Int = 0x01, @U32 uid: Long, @U32 timestamp: Long, @U8 unlockType: Int): Command<ScooterUnlockResult>

    /**
     * 开锁回复
     * @param mode 0x02, 回复
     */
    @NoResponse
    @CommandID(0x05)
    fun unlockReply(@U8 mode: Int = 0x02): Command<Void>

    /**
     * 关锁
     */
    @CommandID(0x15)
    fun lock(@U8 mode: Int = 0x01): Command<ScooterLockResult>

    /**
     * 关锁回复
     */
    @NoResponse
    @CommandID(0x15)
    fun lockReply(@U8 mode: Int = 0x02): Command<Void>

    /**
     * 电源控制
     * @param mode 0x01关机 0x02开机
     */
    @NoResponse
    @CommandID(0x91)
    fun powerControl(@U8 mode: Int): Command<Void>

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
//    @CommandID(0, 0xFC)
    fun endWrite(@U16 pack: Int, data: ByteArray): TransmissionCommand<Void>

    /**
     * 获取锁信息
     */
    @CommandID(0x31)
    fun getLockInfo(@U8 control: Int = 0x01): Command<ScooterLockInfoResult>

    /**
     * 获取固件信息包数
     */
    @CommandID(0xFA, 0xFB)
    fun startRead(): Command<ScooterStartReadResult>

    /**
     * 读取固件
     */
    @CommandID(0xFC, 0xFB)
    fun read(@U16 pack: Int, @U8 deviceType: Int): Command<ReadResult>

    /**
     * 读取电池信息
     */
    @CommandID(0xC7, 0xFB)
    fun startReadBatteryInfo(@U8 operation: Int = 0x01): Command<ScooterStartReadResult>

    /**
     * 读取旧数据
     */
    @CommandID(0x51)
    fun getOldData(@U8 data: Int = 0x01): Command<ScooterOldDataResult>

    /**
     * 清除旧数据
     */
    @CommandID(0x52)
    fun cleanOldData(@U8 data: Int = 0x01): Command<Boolean>

    /**
     * 获取滑板车信息
     */
    @CommandID(0x60)
    fun getScooterInfo(@U8 data: Int = 0x01): Command<ScooterInfoResult>

    /**
     * 滑板车设置
     * @param light 大灯开关 0:无效（不设置） 1:关闭 2:开启
     * @param speedMode 模式设置 0:无效（不设置） 1:低速 2:中速 3:高速
     * @param throttleResponse 油门响应 0:无效（不设置） 1:关闭 2:开启
     * @param taillightFlashing 尾灯闪烁 0:无效（不设置） 1:关闭 2:开启
     */
    @CommandID(0x61)
    fun setScooterConfig(@U8 light: Int = 0, @U8 speedMode: Int = 0, @U8 throttleResponse: Int = 0, @U8 taillightFlashing: Int = 0): Command<Boolean>

    /**
     * 外部锁控制
     * 0x01->电池锁解锁 0x02->车轮锁解锁 0x03->钢缆锁解锁
     * 0x11->电池锁上锁 0x12->车轮锁上锁 0x13->钢缆锁上锁
     */
    @CommandID(0x81)
    fun setOutLockControl(@U8 operation: Int): Command<ScooterOutLockControlResult>

    /**
     * 获取电池锁状态
     */
    @CommandID(0x81)
    fun getBatteryStatus(@U8 operation: Int = 0x21): Command<ScooterBatteryLockStatusResult>

    /**
     * 彩灯控制
     * @param light 彩灯开关状态 0:无效,不设置此项 1:关闭 2:开启
     * @param rgb 静态颜色 RGB 值(4Byte) 0:无效,不设置 例:0x00A1A2A3->R(A1) G(A2) B(A3)
     * @param speed 速度调节 0:无效,不设置此项 1-255 速度值
     * @param brightness 亮度值 0:无效,不设置此项 1-255 亮度值
     * @param mode 效果模式 0:无效,不设置此项 1-N 效果模式
     */
    @CommandID(0x82)
    fun lanternControl(@U8 light: Int = 0, @U32 rgb: Long = 0, @U8 speed: Int = 0, @U8 brightness: Int = 0, @U8 mode: Int = 0): Command<Boolean>

    /**
     * RGB测试
     */
    @CommandID(0xC7)
    fun rgbTest(@U8 operation: Int = 0x02): Command<ScooterRGBTestResult>

    /**
     * 实时读取日志
     */
    @CommandID(0xC7)
    fun readLog(@U8 deviceType: Int): LogCommand<String>

    /**
     * 滑板车配置
     */
    @CommandID(0x62)
    fun scooterConfig(@U8 save: Int, @U8 cruise: Int, @U8 startMode: Int, @U8 low: Int, @U8 middle: Int, @U8 high: Int): Command<Boolean>


}