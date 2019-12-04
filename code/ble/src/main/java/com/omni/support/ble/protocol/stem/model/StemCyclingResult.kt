package com.omni.support.ble.protocol.stem.model

import com.omni.support.ble.core.BufferDeserializable
import com.omni.support.ble.utils.BufferConverter

/**
 * @author 邱永恒
 *
 * @time 2019/11/12 16:03
 *
 * @desc
 *
 */
class StemCyclingResult: BufferDeserializable {
    // 是否在骑行
    var isRiding: Boolean = false
    //最近一次骑行时间
    var startDate: String = ""
    // 骑行时间(秒)
    var ridingTime: Int = 0
    // 休息时间(秒)
    var restTime: Int = 0
    // 实时速度
    var curSpeed: Int = 0
    // 最高速度
    var maxSpeed: Int = 0
    // 最低速度
    var minSpeed: Int = 0
    // 平均速度
    var avgSpeed: Int = 0
    // 骑行里程(0.1KM)
    var distance: Int = 0

    /** 踏频器专有 */
    // 踏频(10s更新一次)
    var cadence: Int = 0
    // 电量
    var battery: Int = 0

    override fun setBuffer(buffer: ByteArray) {
        if (buffer.size != 20) {
            return
        }

        val bc = BufferConverter(buffer)
        isRiding = bc.u8 == 1
        startDate = String.format("20%02d-%02d-%02d %02d:%02d:%02d", bc.u8, bc.u8, bc.u8, bc.u8, bc.u8, bc.u8)
        ridingTime = bc.u16
        restTime = bc.u16
        curSpeed = bc.u8
        maxSpeed = bc.u8
        minSpeed = bc.u8
        avgSpeed = bc.u8
        distance = bc.u16
        cadence = bc.u8
        battery = bc.u8
    }

    override fun toString(): String {
        return "StemCyclingResult(isRiding=$isRiding, startDate='$startDate', ridingTime=$ridingTime, restTime=$restTime, curSpeed=$curSpeed, maxSpeed=$maxSpeed, minSpeed=$minSpeed, avgSpeed=$avgSpeed, distance=$distance, cadence=$cadence, battery=$battery)"
    }
}