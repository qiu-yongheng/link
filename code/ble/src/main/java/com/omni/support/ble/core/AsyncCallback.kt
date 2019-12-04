package com.omni.support.ble.core

/**
 * @author 邱永恒
 *
 * @time 2019/1/19  15:10
 *
 * @desc 在一段时间内, 异步接收多个蓝牙回调
 *
 */

interface AsyncCallback<T> {
    fun onStarted(success: Boolean)

    fun onReceiving(call: ISessionCall<T>, data: IResp<T>)

    fun onReceived()

    fun onTimeout()
}