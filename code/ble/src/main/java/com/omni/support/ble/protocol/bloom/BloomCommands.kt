package com.omni.support.ble.protocol.bloom

import com.omni.support.ble.protocol.Command
import com.omni.support.ble.protocol.bloom.model.BloomResult
import com.omni.support.ble.rover.annotations.CommandID
import com.omni.support.ble.rover.annotations.NoResponse
import com.omni.support.ble.rover.annotations.U32
import com.omni.support.ble.rover.annotations.U8

/**
 * @author 邱永恒
 *
 * @time 2019/8/29 16:33
 *
 * @desc TODO 钢缆锁
 *
 */
interface BloomCommands {

    /**
     * 用来接收返回的数据
     * 使用subscribe操作符
     */
    @CommandID(0xFF)
    fun recv(): Command<BloomResult>

    /**
     * 设备存储有key, 发送校验
     */
    @NoResponse
    @CommandID(0x01)
    fun checkKey(inputKey: ByteArray): Command<Void>

    /**
     * 设置key
     */
    @NoResponse
    @CommandID(0x01)
    fun setKey(inputKey: ByteArray): Command<Void>

    /**
     * 擦除key
     */
    @NoResponse
    @CommandID(0x02)
    fun deleteKey(@U32 data: Long = 0x00): Command<Void>

    /**
     * 开锁
     */
    @NoResponse
    @CommandID(0x05)
    fun unlock(@U8 data: Int = 0x80): Command<Void>

    /**
     * 开锁(带时间戳)
     */
    @NoResponse
    @CommandID(0x09)
    fun unlock(@U32 timestamp: Long): Command<Void>

    /**
     * 蓝牙关机
     */
    @NoResponse
    @CommandID(0x06)
    fun shutdown(@U8 data: Int = 0x30): Command<Void>

    /**
     * 开机模式
     * @param data 0x10 APP + 按钮; 0x20 仅APP
     */
    @NoResponse
    @CommandID(0x05)
    fun setUnlockMode(@U8 data: Int): Command<Void>

    /**
     * 发送心跳包
     */
    @NoResponse
    @CommandID(0x07)
    fun heartbeat(@U8 data: Int = 0x00): Command<Void>
}