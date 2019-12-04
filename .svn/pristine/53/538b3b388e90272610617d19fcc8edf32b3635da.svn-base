package com.omni.support.ble.core


/**
 * @author 邱永恒
 *
 * @time 2019/8/6 17:29
 *
 * @desc 通讯链路
 *
 */
interface ILink {
    fun open()

    fun close()

    fun isOpen(): Boolean

    fun sendTo(pack: IPack)

    interface OnReceivedListener {
        fun onReceived(pack: IPack)
    }

    interface OnErrorListener {
        fun onError(e: Throwable)
    }
}