package com.omni.support.ble.profile

import no.nordicsemi.android.ble.callback.DataReceivedCallback

/**
 * @author 邱永恒
 *
 * @time 2019/11/11 16:16
 *
 * @desc
 *
 */
interface IProfileManager {
    fun setReceiver(receiver: DataReceivedCallback)
    fun setProfileCallback(callback: OmniBleManagerCallbacks)
    fun connect(mac: String)
    fun disConnect()
    fun isConnect(): Boolean
    fun send(data: ByteArray)
    fun read()
}