package com.omni.support.ble.protocol.hj

import com.omni.support.ble.protocol.Command
import com.omni.support.ble.protocol.hj.model.*
import com.omni.support.ble.rover.annotations.CommandID
import com.omni.support.ble.rover.annotations.NoResponse
import com.omni.support.ble.rover.annotations.U8

/**
 * @author 邱永恒
 *
 * @time 2019/9/24 11:58
 *
 * @desc
 *
 */
interface HJCommands {

    /**
     * 获取令牌
     */
    @CommandID(0x0601)
    fun getToken(@U8 data: Int = 0x01): Command<HJTokenResult>

    /**
     * 修改广播名称
     * @param name 最长9个字节
     */
    @CommandID(0x0401, 0x0402)
    fun modifyName(name: ByteArray): Command<Boolean>

    /**
     * 获取电量
     */
    @CommandID(0x0201)
    fun getBattery(@U8 data: Int = 0x01): Command<HJBatteryResult>

    /**
     * 查询开锁状态
     */
    @CommandID(0x050E)
    fun getLockStatus(@U8 data: Int = 0x01): Command<HJLockStatusResult>

    /**
     * 开锁
     */
    @CommandID(0x0501)
    fun unlock(pwd: String): Command<HJUlockResult>

    /**
     * 关锁
     */
    @CommandID(0x050C)
    fun lock(@U8 data: Int = 0x00): Command<HJLockResult>

    /**
     * 关锁回复
     */
    @NoResponse
    @CommandID(0x050C)
    fun lockReply(@U8 data: Int = 0x00): Command<Void>

    /**
     * 查询工作模式
     */
    @CommandID(0x0520)
    fun getWorkMode(@U8 data: Int = 0x01): Command<HJWorkModeResult>

    /**
     * 设置工作模式
     * @param mode 0正常模式；01睡眠模式；02重启
     */
    @CommandID(0x0521)
    fun setWorkMode(@U8 mode: Int): Command<HJSetWorkModeResult>
}