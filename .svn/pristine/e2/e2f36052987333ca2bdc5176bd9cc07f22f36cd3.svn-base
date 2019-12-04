package com.omni.support.ble.core

/**
 * @author 邱永恒
 *
 * @time 2019/8/6 17:35
 *
 * @desc 响应数据
 *
 */
interface IResponse {
    fun reset()

    fun await(timeoutMillions: Long): Boolean

    fun setResult(buffer: ByteArray)

    fun getResult(): ByteArray

    fun setResultCall(call: ResponseCall?)

    interface ResponseCall {
        fun onResult(buffer: ByteArray)
    }
}