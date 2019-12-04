package com.omni.support.ble.protocol.stem

import com.omni.support.ble.protocol.Command
import com.omni.support.ble.protocol.stem.model.*
import com.omni.support.ble.rover.annotations.CommandID
import com.omni.support.ble.rover.annotations.NoResponse

/**
 * @author 邱永恒
 *
 * @time 2019/11/11 15:18
 *
 * @desc
 *
 */
interface StemCommands {
    /**
     * 配置把立
     */
    @CommandID(StemPack.PREFIX_CONFIG)
    fun setConfig(model: StemConfigModel): Command<StemConfigModel>

    /**
     * 接收骑行数据
     * 使用subscribe操作符
     */
    @CommandID(StemPack.PREFIX_CYCLING)
    fun recvCycling(): Command<StemCyclingResult>

    /**
     * 接收传感器数据
     * 使用subscribe操作符
     */
    @CommandID(StemPack.PREFIX_SENSOR)
    fun recvSensor(): Command<StemSensorResult>

    /**
     * 接收踏频计数数据
     * 使用subscribe操作符
     */
    @CommandID(StemPack.PREFIX_COUNT)
    fun recvCount(): Command<StemCountResult>

    /**
     * 发送控制指令
     */
    @NoResponse
    @CommandID(StemPack.PREFIX_CONTROL)
    fun sendControl(param: StemControlparam): Command<Void>
}