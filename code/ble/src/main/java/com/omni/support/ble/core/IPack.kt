package com.omni.support.ble.core

/**
 * @author 邱永恒
 *
 * @time 2018/12/17  17:30
 *
 * @desc 数据包
 *
 */

interface IPack {
    /**
     * 序列化
     *
     * @return 字节数组
     */
    fun getBuffer(): ByteArray

    /**
     * 反序列化
     *
     * @param buffer 字节数组
     */
    fun setBuffer(buffer: ByteArray)
}